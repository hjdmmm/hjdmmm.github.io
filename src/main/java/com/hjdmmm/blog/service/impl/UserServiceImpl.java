package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.dao.UserDAO;
import com.hjdmmm.blog.domain.dto.UserDTO;
import com.hjdmmm.blog.domain.entity.User;
import com.hjdmmm.blog.domain.model.AddOrEditUserModel;
import com.hjdmmm.blog.domain.vo.PageVO;
import com.hjdmmm.blog.service.HashEncoder;
import com.hjdmmm.blog.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final HashEncoder hashEncoder;

    private final UserDAO userDAO;

    private static User convertModel2Entity(AddOrEditUserModel model) {
        User user = new User();
        user.setUsername(model.username());
        user.setPassword(model.password());
        user.setType(model.type());
        user.setStatus(model.status());
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(AddOrEditUserModel model, long modifier) throws Exception {
        User user = convertModel2Entity(model);
        user.setCreateBy(modifier);
        user.setUpdateBy(modifier);

        boolean usernameExist = userDAO.countByUsernameForUpdate(user.getUsername()) > 0;
        if (usernameExist) {
            return false;
        }
        user.setPassword(hashEncoder.encode(user.getPassword()));

        userDAO.insert(user);

        return true;
    }

    @Override
    public void delete(long id, long modifier) throws Exception {
        userDAO.delete(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean edit(long id, AddOrEditUserModel model, long modifier) throws Exception {
        User user = convertModel2Entity(model);
        user.setId(id);
        user.setUpdateBy(modifier);

        boolean usernameExist = userDAO.countByUsernameForUpdate(user.getUsername()) > 0L;
        if (usernameExist) {
            return false;
        }

        user.setPassword(hashEncoder.encode(user.getPassword()));

        userDAO.updateById(user);

        return true;
    }

    @Override
    public void changeStatus(long id, int status, long modifier) throws Exception {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        user.setUpdateBy(modifier);
        userDAO.updateById(user);
    }

    @Override
    public UserDTO get(long id) throws Exception {
        return userDAO.select(id);
    }

    @Override
    public PageVO<UserDTO> list(int pageNum, int pageSize, String username, Integer type, Integer status) throws Exception {
        return userDAO.pageSelect(pageNum, pageSize, username, type, status);
    }
}

