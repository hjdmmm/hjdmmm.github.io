package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.dao.LinkDAO;
import com.hjdmmm.blog.domain.entity.Link;
import com.hjdmmm.blog.domain.vo.BlogLinkListVO;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.service.LinkService;
import com.hjdmmm.blog.util.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinkServiceImpl implements LinkService {
    private final LinkDAO linkDAO;

    public LinkServiceImpl(LinkDAO linkDAO) {
        this.linkDAO = linkDAO;
    }

    @Override
    public void add(Link link) {
        linkDAO.insert(link);
    }

    @Override
    public void delete(long id) {
        linkDAO.delete(id);
    }

    @Override
    public void edit(Link link) {
        linkDAO.updateById(link);
    }

    @Override
    public void changeStatus(long id, Integer status) {
        linkDAO.update(id, status);
    }

    @Override
    public Link get(long id) {
        return linkDAO.select(id);
    }

    @Override
    public PageVO<Link> list(int pageNum, int pageSize, String name, Integer status) {
        return linkDAO.pageSelect(pageNum, pageSize, name, status);
    }

    @Override
    public List<BlogLinkListVO> blogListAll() {
        List<Link> linkList = linkDAO.selectNormal();
        return BeanUtils.copyBeanList(linkList, BlogLinkListVO.class);
    }
}
