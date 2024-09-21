package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.dao.TagDAO;
import com.hjdmmm.blog.domain.entity.Tag;
import com.hjdmmm.blog.domain.vo.LinkListAllVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.service.TagService;
import com.hjdmmm.blog.util.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagDAO tagDAO;

    public TagServiceImpl(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
    }

    @Override
    public void add(Tag tag) {
        tagDAO.insert(tag);
    }

    @Override
    public void delete(long id) {
        tagDAO.delete(id);
    }

    @Override
    public void edit(Tag tag) {
        tagDAO.updateById(tag);
    }

    @Override
    public Tag get(long id) {
        return tagDAO.select(id);
    }

    @Override
    public PageVO<Tag> list(int pageNum, int pageSize, String name, String remark) {
        return tagDAO.pageSelect(pageNum, pageSize, name, remark);
    }

    @Override
    public List<LinkListAllVO> listAll() {
        List<Tag> tagList = tagDAO.selectAll();
        return BeanUtils.copyBeanList(tagList, LinkListAllVO.class);
    }
}

