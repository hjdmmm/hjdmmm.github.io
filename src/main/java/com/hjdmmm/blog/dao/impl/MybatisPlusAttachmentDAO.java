package com.hjdmmm.blog.dao.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hjdmmm.blog.dao.AttachmentDAO;
import com.hjdmmm.blog.dao.impl.mapper.AttachmentMapper;
import com.hjdmmm.blog.domain.entity.Attachment;
import com.hjdmmm.blog.domain.vo.PageVO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class MybatisPlusAttachmentDAO implements AttachmentDAO {
    private final MybatisPlusServiceImpl mybatisPlusService;

    public MybatisPlusAttachmentDAO(MybatisPlusServiceImpl mybatisPlusService) {
        this.mybatisPlusService = mybatisPlusService;
    }

    @Override
    public void insert(Attachment attachment) {
        mybatisPlusService.save(attachment);
    }

    @Override
    public void delete(long id) {
        mybatisPlusService.removeById(id);
    }

    @Override
    public void update(long id, String url) {
        mybatisPlusService.lambdaUpdate().eq(Attachment::getId, id).set(Attachment::getUrl, url).update();
    }

    @Override
    public void updateById(Attachment attachment) {
        mybatisPlusService.updateById(attachment);
    }

    @Override
    public Attachment select(long id) {
        return mybatisPlusService.getById(id);
    }

    @Override
    public PageVO<Attachment> pageSelect(int pageNum, int pageSize, String name, String mimeTypePrefix) {
        Page<Attachment> page = mybatisPlusService.lambdaQuery()
                .like(StringUtils.hasText(name), Attachment::getOriginalName, name)
                .likeRight(StringUtils.hasText(mimeTypePrefix), Attachment::getMimeType, mimeTypePrefix)
                .page(new Page<>(pageNum, pageSize));

        return new PageVO<>(page.getRecords(), page.getTotal());
    }

    @Component
    public static class MybatisPlusServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> {
    }
}
