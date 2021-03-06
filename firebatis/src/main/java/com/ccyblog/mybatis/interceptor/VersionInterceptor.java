package com.ccyblog.mybatis.interceptor;

import com.ccyblog.mybatis.annotation.EnableLock;
import com.ccyblog.mybatis.exception.NoRowAffectException;
import com.ccyblog.mybatis.util.PluginUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperMethod;
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
 * @author isghost
 * @version 2018/3/19 10:03
 * todo 抛出指定的异常， 目前不管抛出什么异常，都会被转换为MyBatisSystemException
 * todo 函数查找使用缓存
 */
@Intercepts(
    {
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})
    }
)
public class VersionInterceptor implements Interceptor {

    private static final Log log = LogFactory.getLog(VersionInterceptor.class);

    private static final String DEFAULT_VERSION_NAME = "version";
    private String versionColumn = DEFAULT_VERSION_NAME;
    private String versionField = DEFAULT_VERSION_NAME;
    private Boolean defaultEnableLock = false;
    private Boolean defaultThrowException = false;

    private static final String UPDATE = "update";
    private static final String PREPARE = "prepare";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) PluginUtils.getTarget(invocation);
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        BoundSql boundSql = statementHandler.getBoundSql();
        String id = ms.getId();

        if (ms.getSqlCommandType() != SqlCommandType.UPDATE) {
            return invocation.proceed();
        }
        String name = invocation.getMethod().getName();
        EnableLock lockAnnotation = getEnableLock(id, boundSql);
        if (PREPARE.equals(name)) {
            handlerPrepare(metaObject, boundSql, lockAnnotation);
        }
        Object result = invocation.proceed();

        if (UPDATE.equals(name)) {
            handlerAfterUpdate(lockAnnotation, result);
        }

        return result;
    }

    /**
     * 处理查询前的sql
     */
    private void handlerPrepare(MetaObject metaObject, BoundSql boundSql, EnableLock lockAnnotation)
        throws JSQLParserException {
        boolean enableLock = defaultEnableLock;
        if (lockAnnotation != null) {
            enableLock = lockAnnotation.value();
        }
        if (!enableLock) {
            return;
        }
        Update update = (Update) CCJSqlParserUtil.parse(boundSql.getSql());
        if (update.getColumns().stream().anyMatch(column -> column.getColumnName().equalsIgnoreCase(versionColumn))) {
            return;
        }
        try {
            Object originalVersion = metaObject.getValue("delegate.boundSql.parameterObject." + versionField);
            metaObject.setValue("delegate.boundSql.sql", createNewSql(update, originalVersion));
        } catch (BindingException ignore) {
            // nothing
        }

    }

    /**
     * 获得EnableLock
     */
    private EnableLock getEnableLock(String id, BoundSql boundSql) throws ClassNotFoundException {
        Object paramObject = boundSql.getParameterObject();
        Class<?>[] clsArray = null;
        if (paramObject instanceof MapperMethod.ParamMap<?>) {
            MapperMethod.ParamMap<?> paramMap = (MapperMethod.ParamMap<?>) paramObject;
            if (!paramMap.isEmpty()) {
                int len = paramMap.size() / 2;
                clsArray = new Class<?>[len];
                for (int i = 0; i < len; i++) {
                    clsArray[i] = paramMap.get("param" + (i + 1)).getClass();
                }
            }
        } else if (paramObject instanceof Map) {
            return null;
        } else {
            clsArray = new Class<?>[]{paramObject.getClass()};
        }
        int pos = id.lastIndexOf('.');
        Class<?> daoClass = Class.forName(id.substring(0, pos));
        String methodName = id.substring(pos + 1);
        Method method = getMethod(daoClass, methodName, clsArray);
        if (method == null) {
            log.error("[firebatis]method couldn't found");
            throw new NullPointerException("method couldn't found");
        }
        return method.getAnnotation(EnableLock.class);
    }

    /**
     * 根据参数列表获取对应的方法
     */
    private Method getMethod(Class<?> daoClass, String methodName, Class<?>[] clsArray) {
        Method[] declaredMethods = daoClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            Parameter[] parameters = method.getParameters();
            if (!method.getName().equals(methodName) || parameters.length != clsArray.length) {
                continue;
            }
            boolean flag = true;
            for (int i = 0; i < parameters.length; i++) {
                if (!parameters[i].getType().isAssignableFrom(clsArray[i])) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                return method;
            }
        }
        return null;
    }

    /**
     * sql添加version
     */
    private String createNewSql(Update update, Object version) {
        addSetExpression(update);
        createCondition(update, version.toString());
        if (log.isDebugEnabled()) {
            log.debug("[firebatis] create new sql: " + update.toString());
        }
        return update.toString();

    }

    /**
     * 添加set version = version + 1
     */
    private void addSetExpression(Update update) {
        List<Column> columns = update.getColumns();
        Column column = new Column();
        column.setColumnName(this.versionColumn);
        columns.add(column);

        List<Expression> expressions = update.getExpressions();
        Addition add = new Addition();
        add.setLeftExpression(column);
        add.setRightExpression(new LongValue(1));
        expressions.add(add);
    }

    /**
     * 添加where version = version
     */
    private void createCondition(Update update, String version) {
        EqualsTo equal = new EqualsTo();
        Column column = new Column();
        column.setColumnName(versionColumn);
        equal.setLeftExpression(column);
        LongValue val = new LongValue(version);
        equal.setRightExpression(val);

        Expression where = update.getWhere();
        if (where != null) {
            AndExpression and = new AndExpression(where, equal);
            update.setWhere(and);
        } else {
            update.setWhere(equal);
        }
        return;
    }

    /**
     * 更新后，如果没有一行发生改变，则抛出异常
     */
    private void handlerAfterUpdate(EnableLock lockAnnotation, Object result) {
        boolean enableException = defaultThrowException;
        if (lockAnnotation != null) {
            enableException = lockAnnotation.enableException();
        }
        if (enableException && !result.equals(1)) {
            throw new NoRowAffectException("update failed, because no row effect");
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * @param properties 配置的属性
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setProperties(Properties properties) {
        if (properties == null) {
            return;
        }
        versionColumn = properties.getProperty("versionColumn", DEFAULT_VERSION_NAME);
        versionField = properties.getProperty("versionField", DEFAULT_VERSION_NAME);

        String enableLockStr = properties.getProperty("defaultEnableLock");
        if (enableLockStr != null) {
            defaultEnableLock = Boolean.valueOf(enableLockStr);
        }

        String enableExceptionStr = properties.getProperty("enableException");
        if (enableLockStr != null) {
            defaultThrowException = Boolean.valueOf(enableExceptionStr);
        }

    }
}
