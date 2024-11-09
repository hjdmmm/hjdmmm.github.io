package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.config.CommentConfig;
import com.hjdmmm.blog.dao.ArticleDAO;
import com.hjdmmm.blog.dao.CommentDAO;
import com.hjdmmm.blog.dao.UserDAO;
import com.hjdmmm.blog.domain.entity.Article;
import com.hjdmmm.blog.domain.entity.Comment;
import com.hjdmmm.blog.domain.entity.User;
import com.hjdmmm.blog.domain.vo.BlogCommentVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.exception.IllegalArticleCommentException;
import com.hjdmmm.blog.service.CommentService;
import com.hjdmmm.blog.util.BeanUtils;
import com.hjdmmm.blog.util.BooleanUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private final ConcurrentMap<String, Integer> ip2CommentCountMap = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Instant> ip2CommentInstantMap = new ConcurrentHashMap<>();

    private final ArticleDAO articleDAO;

    private final CommentConfig commentConfig;

    private final CommentDAO commentDAO;

    private final UserDAO userDAO;

    public CommentServiceImpl(ArticleDAO articleDAO, CommentConfig commentConfig, CommentDAO commentDAO, UserDAO userDAO) {
        this.articleDAO = articleDAO;
        this.commentConfig = commentConfig;
        this.commentDAO = commentDAO;
        this.userDAO = userDAO;
    }

    private static String getNickName(User user) {
        if (user == null) {
            return "匿名用户";
        }

        return user.getNickName();
    }

    private static BlogCommentVO buildCommentVO(Comment comment, List<Comment> childrenComments, List<User> users) {
        BlogCommentVO blogCommentVO = BeanUtils.copyBean(comment, BlogCommentVO.class);

        User user = users.stream()
                .filter(e -> e.getId().equals(comment.getCreateBy()))
                .findFirst()
                .orElse(null);
        blogCommentVO.setNickName(getNickName(user));

        if (!Long.valueOf(Comment.ROOT_PID).equals(blogCommentVO.getPid())) {
            User toCommentUser = users.stream()
                    .filter(e -> e.getId().equals(comment.getToCommentUserId()))
                    .findFirst()
                    .orElse(null);
            blogCommentVO.setToCommentUserNickName(getNickName(toCommentUser));
        }

        if (childrenComments == null) {
            return blogCommentVO;
        }

        List<BlogCommentVO> children = childrenComments.stream()
                .filter(e -> comment.getId().equals(e.getPid()))
                .map(e -> buildCommentVO(e, null, users))
                .collect(Collectors.toList());
        blogCommentVO.setChildren(children);

        return blogCommentVO;
    }

    @Override
    public void add(Comment comment) throws IllegalArticleCommentException {
        if (Integer.valueOf(Comment.ARTICLE_COMMENT).equals(comment.getType())) {
            boolean articleCanComment = Optional.of(comment)
                    .map(Comment::getArticleId)
                    .map(articleDAO::select)
                    .map(Article::getComment)
                    .map(BooleanUtils::toBoolean)
                    .orElse(false);

            if (!articleCanComment) {
                throw new IllegalArticleCommentException();
            }
        }

        commentDAO.insert(comment);
    }

    @Override
    public PageVO<BlogCommentVO> getArticleComments(int pageNum, int pageSize, long articleId) {
        PageVO<Comment> commentPageVO = commentDAO.pageSelectRootByArticleId(pageNum, pageSize, articleId);

        List<Comment> commentList = commentPageVO.getRows();

        List<Comment> childrenComments;
        if (commentList.isEmpty()) {
            childrenComments = Collections.emptyList();
        } else {
            childrenComments = commentDAO.selectByPids(commentList.stream().map(Comment::getId).collect(Collectors.toList()));
        }

        List<User> users;
        if (commentList.isEmpty()) {
            users = Collections.emptyList();
        } else {
            List<Comment> allComments = new ArrayList<>(commentList);
            allComments.addAll(childrenComments);

            Set<Long> userIdSet = allComments.stream().map(Comment::getCreateBy).collect(Collectors.toSet());

            userIdSet.addAll(childrenComments.stream()
                    .map(Comment::getToCommentUserId)
                    .collect(Collectors.toSet()));

            users = userDAO.select(userIdSet);
        }

        List<BlogCommentVO> blogCommentVOList = commentList.stream().map(comment -> buildCommentVO(comment, childrenComments, users)).collect(Collectors.toList());

        return commentPageVO.convertType(blogCommentVOList);
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

