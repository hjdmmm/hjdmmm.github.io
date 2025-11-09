package com.hjdmmm.blog.dao.impl;

import com.hjdmmm.blog.dao.AttachmentDAO;
import com.hjdmmm.blog.dao.impl.repository.AttachmentRepository;
import com.hjdmmm.blog.domain.entity.Attachment;
import com.hjdmmm.blog.domain.entity.Attachment_;
import com.hjdmmm.blog.domain.vo.PageVO;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@AllArgsConstructor
@Repository
public class JpaAttachmentDAO implements AttachmentDAO {
    private final AttachmentRepository attachmentRepository;

    @Override
    public void insert(Attachment attachment) throws Exception {
        attachmentRepository.saveAndFlush(attachment);
    }

    @Override
    public void delete(long id) throws Exception {
        attachmentRepository.deleteById(id);
    }

    @Override
    public void updateById(Attachment attachment) throws Exception {
        attachmentRepository.save(attachment);
    }

    @Override
    public Attachment select(long id) throws Exception {
        return attachmentRepository.findById(id).orElse(null);
    }

    @Override
    public PageVO<Attachment> pageSelect(int pageNum, int pageSize, String name, String mimeTypePrefix) throws Exception {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);

        Specification<Attachment> spec = (Root<Attachment> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (!StringUtils.hasText(mimeTypePrefix)) {
                return null;
            }

            return cb.like(root.get(Attachment_.mimeType), mimeTypePrefix + "%");
        };

        Page<Attachment> pageResult = attachmentRepository.findAll(spec, pageRequest);

        return new PageVO<>(pageResult.getContent(), pageResult.getTotalElements());
    }
}
