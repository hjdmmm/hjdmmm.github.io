package com.hjdmmm.blog.dao.impl;

import com.hjdmmm.blog.dao.CommentDAO;
import com.hjdmmm.blog.dao.impl.repository.CommentRepository;
import com.hjdmmm.blog.domain.entity.Comment;
import com.hjdmmm.blog.domain.entity.Comment_;
import com.hjdmmm.blog.domain.vo.PageVO;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@AllArgsConstructor
@Repository
public class JpaCommentDAO implements CommentDAO {
    private final CommentRepository commentRepository;

    @Override
    public void insert(Comment comment) throws Exception {
        commentRepository.saveAndFlush(comment);
    }

    @Override
    public void delete(long id) throws Exception {
        commentRepository.deleteById(id);
    }

    @Override
    public Comment select(long id) throws Exception {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Comment> selectByPids(List<Long> pids) throws Exception {
        return commentRepository.findAllByPidIn(pids);
    }

    @Override
    public PageVO<Comment> pageSelectRootByArticleId(int pageNum, int pageSize, long articleId) throws Exception {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, Comment_.CREATE_TIME));

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setPid(-1L);

        ExampleMatcher matcher = ExampleMatcher.matching()
            .withMatcher(Comment_.ARTICLE_ID, ExampleMatcher.GenericPropertyMatchers.exact())
            .withMatcher(Comment_.PID, ExampleMatcher.GenericPropertyMatchers.exact());

        Example<Comment> example = Example.of(comment, matcher);

        Page<Comment> pageResult = commentRepository.findAll(example, pageRequest);

        return new PageVO<>(pageResult.getContent(), pageResult.getTotalElements());
    }
}
