package com.hjdmmm.blog.dao.impl;

import com.hjdmmm.blog.dao.TagDAO;
import com.hjdmmm.blog.dao.impl.repository.TagRepository;
import com.hjdmmm.blog.domain.entity.Tag;
import com.hjdmmm.blog.domain.entity.Tag_;
import com.hjdmmm.blog.domain.vo.PageVO;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class JpaTagDAO implements TagDAO {
    private final TagRepository tagRepository;

    @Override
    public void insert(Tag tag) throws Exception {
        tagRepository.saveAndFlush(tag);
    }

    @Override
    public void delete(long id) throws Exception {
        tagRepository.deleteById(id);
    }

    @Override
    public void updateById(Tag tag) throws Exception {
        tagRepository.save(tag);
    }

    @Override
    public Tag select(long id) throws Exception {
        return tagRepository.findById(id).orElse(null);
    }

    @Override
    public List<Tag> selectAll(List<Long> tagIds) throws Exception {
        return tagRepository.findAllById(tagIds);
    }

    @Override
    public PageVO<Tag> pageSelect(int pageNum, int pageSize, String nameLike, String remarkLike) throws Exception {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);

        Specification<Tag> spec = (Root<Tag> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(nameLike)) {
                predicates.add(cb.like(root.get(Tag_.name), "%" + nameLike + "%"));
            }

            if (StringUtils.hasText(remarkLike)) {
                predicates.add(cb.like(root.get(Tag_.remark), "%" + remarkLike + "%"));
            }

            if (predicates.isEmpty()) {
                return null;
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Tag> pageResult = tagRepository.findAll(spec, pageRequest);
        return new PageVO<>(pageResult.getContent(), pageResult.getTotalElements());
    }
}
