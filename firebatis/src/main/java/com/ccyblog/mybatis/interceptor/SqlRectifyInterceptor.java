package com.ccyblog.mybatis.interceptor;

import com.ccyblog.mybatis.util.PluginUtils;
import java.sql.Connection;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * @author BG315420
 * @version  2018/3/19 17:31
 *
 *  语法修复
 */
@Intercepts(
    {
        @Signature(type= StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
    }
)
public class SqlRectifyInterceptor implements Interceptor{

    final static Pattern pattern = Pattern.compile("\\w+\\s+in\\s*\\(\\s*\\)");

    private static final Log log = LogFactory.getLog(SqlRectifyInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) PluginUtils.getTarget(invocation);
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        BoundSql boundSql = statementHandler.getBoundSql();

        if(ms.getSqlCommandType() == SqlCommandType.INSERT){
            return invocation.proceed();
        }

        String sql = boundSql.getSql();
        sql = pattern.matcher(sql).replaceAll("false");
        metaObject.setValue("delegate.boundSql.sql", sql);
        log.debug(sql);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
