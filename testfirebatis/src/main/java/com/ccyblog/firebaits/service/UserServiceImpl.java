package com.ccyblog.firebaits.service;

import com.ccyblog.firebaits.dao.UserDao;
import com.ccyblog.firebaits.po.UserPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author isghost
 * @date 2018/1/17.
 * @desc 用户serviceImpl
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Override
    public void login(String username, String password) {
    }

    @Override
    public void logout() {

    }

    @Override
    public UserPO getUserByName(String username) {
        return userDao.getByUserName(username);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = RuntimeException.class)
    @Override
    public void updateUser(UserPO userPO) {
        userDao.updateByPrimaryKey(userPO);
    }
}
