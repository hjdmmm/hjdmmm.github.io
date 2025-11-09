package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.dao.TagDAO;
import com.hjdmmm.blog.domain.entity.Tag;
import com.hjdmmm.blog.domain.model.AddOrEditTagModel;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TagServiceImpl implements TagService {
    private final TagDAO tagDAO;

    private static Tag convertModel2Entity(AddOrEditTagModel model) {
        Tag tag = new Tag();
        tag.setName(model.name());
        tag.setRemark(model.remark());
        return tag;
    }

    @Override
    public void add(AddOrEditTagModel model, long modifier) throws Exception {
        Tag tag = convertModel2Entity(model);
        tag.setCreateBy(modifier);
        tag.setUpdateBy(modifier);
        tagDAO.insert(tag);
    }

    @Override
    public void delete(long id, long modifier) throws Exception {
        tagDAO.delete(id);
    }

    @Override
    public void edit(long id, AddOrEditTagModel model, long modifier) throws Exception {
        Tag tag = convertModel2Entity(model);
        tag.setId(id);
        tag.setUpdateBy(modifier);
        tagDAO.updateById(tag);
    }

    @Override
    public Tag get(long id) throws Exception {
        return tagDAO.select(id);
    }

    @Override
    public PageVO<Tag> list(int pageNum, int pageSize, String keyword) throws Exception {
        return tagDAO.pageSelect(pageNum, pageSize, keyword, keyword);
    }
}
