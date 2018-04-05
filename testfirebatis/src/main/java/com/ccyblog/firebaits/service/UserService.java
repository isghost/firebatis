package com.ccyblog.firebaits.service;

import com.ccyblog.firebaits.po.UserPO;

/**
 * @author isghost
 * @date 2018/1/17.
 * @desc 用户service
 */
public interface UserService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     */
    void login(String username, String password);

    /**
     * 登出
     */
    void logout();

    /**
     * 根据用户名获得用户信息
     * @param username 用户名
     * @return UserPO
     */
    UserPO getUserByName(String  username);

    /**
     * 更新用户信息，专为并发测试设计
     * @param userPO
     */
    void updateUser(UserPO userPO);
}
