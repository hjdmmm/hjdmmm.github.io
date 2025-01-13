package com.hjdmmm.blog.dao;

import com.hjdmmm.blog.domain.entity.Drawio;
import com.hjdmmm.blog.domain.vo.PageVO;

public interface DrawioDAO {
    void insert(Drawio drawio);

    void delete(long id);

    void updateName(long id, String name);

    Drawio select(long id);

    PageVO<Drawio> pageSelect(int pageNum, int pageSize, String name);
}
