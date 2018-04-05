package com.ccyblog.firebaits.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author isghost
 * @date 2017/12/24.
 * @desc 获取bean的辅助函数
 */
@Component
public class SpringApplicationContext implements ApplicationContextAware{


    static private ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationContext.context = applicationContext;
        return ;
    }

    public static ApplicationContext getContext(){
        return SpringApplicationContext.context;
    }

    public static <T> T getBean(Class<T> clazz){
        return SpringApplicationContext.context.getBean(clazz);
    }
}
