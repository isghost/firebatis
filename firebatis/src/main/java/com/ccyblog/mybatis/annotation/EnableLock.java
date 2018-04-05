package com.ccyblog.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author BG315420
 * @version 2018/3/19 11:22
 * sql自动添加version字段
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableLock {
    boolean value() default true ;
    boolean enableException() default false;
}
