package com.ccyblog.firebaits.constant;

/**
 * @author isghost
 * @date 2018/3/4.
 * @desc 错误代码
 */
public enum  ErrorEnum {

    INTERRUPTED("线程中断"),

    UNKNOWN("发生未知错误");
    /**
     * 错误描述
     */
    private String desc;
    ErrorEnum(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "ErrorEnum{" +
            "desc='" + desc + '\'' +
            '}';
    }
}
