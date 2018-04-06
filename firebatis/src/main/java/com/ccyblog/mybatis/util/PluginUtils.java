package com.ccyblog.mybatis.util;

import java.lang.reflect.Proxy;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * @author isghost
 * @version 2018/3/20 10:39
 * 插件util
 */
public class PluginUtils {

    private PluginUtils(){
    }

    public static Object getTarget(Invocation invocation) {
        Object target = invocation.getTarget();
        while (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            target = metaObject.getValue("h.target");
        }
        return target;
    }
}
