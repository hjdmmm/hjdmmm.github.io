package com.hjdmmm.blog.dao.impl.repository;

import com.hjdmmm.blog.domain.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    long countByUsernameIs(String username) throws Exception;

    List<User> searchByUsernameAndStatus(String username, int status) throws Exception;
}