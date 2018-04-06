package com.ccyblog.mybatis.exception;

/**
 * @author isghost
 * @date 2018/4/6.
 * @desc 更新无任何变化异常
 */
public class NoRowAffectException extends RuntimeException {

    public NoRowAffectException(String message) {
        super(message);
    }
}
