package com.ccyblog.firebaits.exception;

import com.ccyblog.firebaits.constant.ErrorEnum;

/**
 * @author isghost
 * @date 2017/12/23.
 * @desc
 */
public class BizException extends RuntimeException {

    private ErrorEnum errorEnum;
    public BizException(ErrorEnum errorEnum){
        this.errorEnum = errorEnum;
    }

    public ErrorEnum getErrorEnum() {
        return errorEnum;
    }

    @Override
    public String toString() {
        return "BizException{" +
            "errorEnum=" + errorEnum +
            '}';
    }
}
