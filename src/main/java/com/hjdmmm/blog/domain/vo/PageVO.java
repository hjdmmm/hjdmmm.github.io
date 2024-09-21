package com.hjdmmm.blog.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> {
    private List<T> rows;
    private long total;

    public PageVO(List<T> rows) {
        this.rows = rows;
        this.total = rows.size();
    }

    @SuppressWarnings("unchecked")
    public <E> PageVO<E> convertType(List<E> newRows) {
        PageVO<E> pageVO = (PageVO<E>) this;
        pageVO.total = newRows.size();
        pageVO.rows = newRows;
        return pageVO;
    }
}
