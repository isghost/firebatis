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

    /**
     * 当foreach为空的时候，可能会出现
     *  xxx in () and x = 0 或者 xxx in  and x = 0 的两种情况
     *  跟mybatis版本有关，不排除其他可能性
     */
    static final Pattern PATTERN = Pattern.compile("\\w+\\s+in\\s*\\(\\s*\\)|\\w+\\s+in(?!\\s*\\()");

    /**
     * mysql可以直接替换成false,oracle不可以
     */
    private static String FALSE_FORMULA = " 1 = 2 ";

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
        sql = PATTERN.matcher(sql).replaceAll(FALSE_FORMULA);
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
