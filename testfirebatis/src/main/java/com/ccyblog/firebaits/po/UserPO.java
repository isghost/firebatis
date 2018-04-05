package com.ccyblog.firebaits.po;

import lombok.Data;

/**
 * @author isghost
 * @date 2018/01/16.
 * @desc 用户
 */

@Data
public class UserPO extends BasePO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 加密盐
     */
    private String salt;

}