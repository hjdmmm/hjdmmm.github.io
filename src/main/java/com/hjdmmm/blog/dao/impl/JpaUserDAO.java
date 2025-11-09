package com.hjdmmm.blog.dao.impl;

import com.hjdmmm.blog.dao.UserDAO;
import com.hjdmmm.blog.dao.impl.repository.UserRepository;
import com.hjdmmm.blog.domain.dto.UserDTO;
import com.hjdmmm.blog.domain.entity.User;
import com.hjdmmm.blog.domain.entity.User_;
import com.hjdmmm.blog.domain.vo.PageVO;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Repository
public class JpaUserDAO implements UserDAO {
    private final UserRepository userRepository;

    @Override
    public void insert(User user) throws Exception {
        userRepository.saveAndFlush(user);
    }

    @Override
    public void delete(long id) throws Exception {
        userRepository.deleteById(id);
    }

    @Override
    public void updateById(User user) throws Exception {
        userRepository.save(user);
    }

    @Override
    public UserDTO select(long id) throws Exception {
        return userRepository.findBy(Example.of(User.builder().id(id).build()), q -> q.as(UserDTO.class).first()).orElse(null);
    }

    @Override
    public List<UserDTO> selectAll(List<Long> ids) throws Exception {
        Specification<User> spec = (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            CriteriaBuilder.In<Long> inClause = cb.in(root.get(User_.id));
            for (Long id : ids) {
                inClause.value(id);
            }
            return inClause;
        };
        return userRepository.findBy(spec, q -> q.as(UserDTO.class).all());
    }

    @Override
    public PageVO<UserDTO> pageSelect(int pageNum, int pageSize, String username, Integer type, Integer status) throws Exception {
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);
        Specification<User> spec = (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(username)) {
                predicates.add(cb.like(root.get(User_.username), "%" + username + "%"));
            }

            if (type != null) {
                predicates.add(cb.equal(root.get(User_.type), type));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get(User_.status), status));
            }

            if (predicates.isEmpty()) {
                return null;
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<UserDTO> pageResult = userRepository.findBy(spec, fetchableFluentQuery ->
            fetchableFluentQuery.as(UserDTO.class).page(pageRequest));
        return new PageVO<>(pageResult.getContent(), pageResult.getTotalElements());
    }

    @Override
    public long countByUsernameForUpdate(String username) throws Exception {
        return userRepository.countByUsernameIs(username);
    }

    @Override
    public List<User> selectByUsernameAndStatus(String username, int status) throws Exception {
        return userRepository.searchByUsernameAndStatus(username, status);
    }
}
