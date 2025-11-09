package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.config.CommentConfig;
import com.hjdmmm.blog.constant.CommentConstants;
import com.hjdmmm.blog.dao.CommentDAO;
import com.hjdmmm.blog.dao.UserDAO;
import com.hjdmmm.blog.domain.dto.UserDTO;
import com.hjdmmm.blog.domain.entity.Comment;
import com.hjdmmm.blog.domain.model.AddCommentModel;
import com.hjdmmm.blog.domain.vo.BlogCommentVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final ConcurrentMap<String, Integer> ip2CommentCountMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Instant> ip2CommentInstantMap = new ConcurrentHashMap<>();

    private final CommentConfig commentConfig;

    private final CommentDAO commentDAO;

    private final UserDAO userDAO;

    private static String getNickName(UserDTO user) {
        if (user == null) {
            return "匿名用户";
        }

        return user.username();
    }

    private static BlogCommentVO buildCommentVO(Comment comment, List<Comment> childrenComments, List<UserDTO> users) {
        UserDTO user = users.stream()
            .filter(e -> e.id() == comment.getCreateBy())
            .findFirst()
            .orElse(null);

        Long pid = null;
        Long toCommentUserId = null;
        String toCommentUsername = null;
        if (CommentConstants.ROOT_COMMENT_PID != comment.getPid()) {
            UserDTO toCommentUser = users.stream()
                .filter(e -> e.id() == comment.getCreateBy())
                .findFirst()
                .orElse(null);

            pid = comment.getPid();
            toCommentUserId = comment.getToCommentUserId();
            toCommentUsername = getNickName(toCommentUser);
        }

        List<BlogCommentVO> children = null;
        if (childrenComments != null) {
            children = childrenComments.stream()
                .filter(e -> comment.getId().equals(e.getPid()))
                .map(e -> buildCommentVO(e, null, users))
                .toList();
        }

        return BlogCommentVO.builder()
            .id(comment.getId())
            .articleId(comment.getArticleId())
            .pid(pid)
            .content(comment.getContent())
            .toCommentUserId(toCommentUserId)
            .toCommentUsername(toCommentUsername)
            .toCommentId(comment.getToCommentId())
            .createTime(comment.getCreateTime())
            .username(getNickName(user))
            .children(children)
            .build();
    }

    @Override
    public void add(AddCommentModel model, long modifier) throws Exception {
        long pid = CommentConstants.ROOT_COMMENT_PID;
        long toCommentId = CommentConstants.ROOT_COMMENT_PID;
        if (model.pid() != null) {
            pid = model.pid();

            Comment parentComment = commentDAO.select(pid);
            if (parentComment.getPid() == CommentConstants.ROOT_COMMENT_PID) {
                toCommentId = parentComment.getId();
            } else {
                toCommentId = parentComment.getToCommentId();
            }
        }

        Comment comment = new Comment();
        comment.setArticleId(model.articleId());
        comment.setPid(pid);
        comment.setContent(model.content());
        comment.setToCommentId(toCommentId);
        comment.setCreateBy(modifier);
        comment.setUpdateBy(modifier);

        commentDAO.insert(comment);
    }

    @Override
    public PageVO<BlogCommentVO> getArticleComments(int pageNum, int pageSize, long articleId) throws Exception {
        PageVO<Comment> commentPageVO = commentDAO.pageSelectRootByArticleId(pageNum, pageSize, articleId);

        List<Comment> commentList = commentPageVO.rows();

        List<Comment> childrenComments;
        if (commentList.isEmpty()) {
            childrenComments = Collections.emptyList();
        } else {
            childrenComments = commentDAO.selectByPids(commentList.stream().map(Comment::getId).toList());
        }

        List<UserDTO> users;
        if (commentList.isEmpty()) {
            users = Collections.emptyList();
        } else {
            List<Comment> allComments = new ArrayList<>(commentList);
            allComments.addAll(childrenComments);

            Set<Long> userIdSet = allComments.stream().map(Comment::getCreateBy).collect(Collectors.toSet());

            userIdSet.addAll(childrenComments.stream()
                .map(Comment::getToCommentUserId)
                .collect(Collectors.toSet()));

            users = userDAO.selectAll(new ArrayList<>(userIdSet));
        }

        List<BlogCommentVO> blogCommentVOList = commentList.stream().map(comment -> buildCommentVO(comment, childrenComments, users)).toList();

        return PageVO.<BlogCommentVO>builder().rows(blogCommentVOList).total(commentPageVO.total()).build();
    }

    @Override
    public boolean canComment(String ip) {
        deleteExpiredIps();
        int maxCommentNum = commentConfig.getMaxCommentNum();
        int newValue = ip2CommentCountMap.compute(ip, (ignore, oldValue) -> {
            if (oldValue == null) {
                return 1;
            }

            if (oldValue > maxCommentNum) {
                return oldValue;
            }

            return oldValue + 1;
        });
        if (newValue <= commentConfig.getMaxCommentNum()) {
            ip2CommentInstantMap.put(ip, Instant.now());
        }

        return newValue <= maxCommentNum;
    }

    private void deleteExpiredIps() {
        Instant now = Instant.now();
        List<String> list = ip2CommentInstantMap.entrySet().stream()
            .filter(entry -> Duration.between(entry.getValue(), now).compareTo(commentConfig.getExpireSeconds()) > 0)
            .map(Map.Entry::getKey)
            .toList();
        for (String key : list) {
            ip2CommentCountMap.remove(key);
            ip2CommentInstantMap.remove(key);
        }
    }
}

