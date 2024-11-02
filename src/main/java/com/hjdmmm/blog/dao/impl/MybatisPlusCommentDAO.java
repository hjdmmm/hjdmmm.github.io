package com.hjdmmm.blog.dao.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjdmmm.blog.dao.CommentDAO;
import com.hjdmmm.blog.dao.impl.mapper.CommentMapper;
import com.hjdmmm.blog.domain.entity.Comment;
import com.hjdmmm.blog.domain.vo.PageVO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class MybatisPlusCommentDAO implements CommentDAO {
    private final MybatisPlusServiceImpl mybatisPlusService;

    public MybatisPlusCommentDAO(MybatisPlusServiceImpl mybatisPlusService) {
        this.mybatisPlusService = mybatisPlusService;
    }

    @Override
    public void insert(Comment comment) {
        mybatisPlusService.save(comment);
    }

    @Override
    public void delete(long id) {
        mybatisPlusService.removeById(id);
    }

    @Override
    public List<Comment> selectByPids(Collection<Long> pids) {
        return mybatisPlusService.lambdaQuery()
                .in(Comment::getPid, pids)
                .orderByDesc(Comment::getCreateTime)
                .list();
    }

    @Override
    public PageVO<Comment> pageSelectRootByArticleId(int pageNum, int pageSize, long articleId) {
        Page<Comment> page = mybatisPlusService.lambdaQuery()
                .eq(Comment::getType, Comment.ARTICLE_COMMENT)
                .eq(Comment::getArticleId, articleId)
                .eq(Comment::getPid, Comment.ROOT_PID)
                .orderByDesc(Comment::getCreateTime)
                .page(new Page<>(pageNum, pageSize));

        return new PageVO<>(page.getRecords(), page.getTotal());
    }

    @Component
    public static class MybatisPlusServiceImpl extends ServiceImpl<CommentMapper, Comment> {
    }
}
