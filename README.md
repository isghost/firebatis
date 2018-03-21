firebatis
===

介绍
---

一个mybatis插件， 主要功能有

1. 在sql语句中自动添加version，实现乐观锁功能
2. 修复sql中`column in()`的空列表问题，替换为false，防止报语法错误

用法
---

### 安装

#### maven

    <dependency>
        <groupId>com.ccyblog.myfire</groupId>
        <artifactId>firebatis</artifactId>
        <version>0.0.1</version>
    </dependency>

### 用法

#### 全局配置

    <plugins>
        <!-- 乐观锁 -->
        <plugin interceptor="com.ccyblog.mybatis.interceptor.VersionInterceptor">
            <!-- 数据库的version的字段名称 -->
            <property name="versionColumn" value="lockVersion" />
            <!-- POJO对象中version的名称 -->
            <property name="versionField" value="lockVersion" />
            <!-- 全局自动添加version默认开关 -->
            <property name="defaultEnableLock" value="false"/>
            <!-- 当update影响行数为0时，是否抛出异常 -->
            <property name="enableException" value="false"/>
        </plugin>
        <!-- 语法修正 -->
        <plugin interceptor="com.ccyblog.mybatis.interceptor.SqlRectifyInterceptor">
        </plugin>
    </plugins>

#### 注解

通过`EnableLock`注解可以修改某个dao函数的行为。 比如

    public interface UserDao {
        @EnableLock(value=true,enableException = true)
        void update(UserPO userPO);
    }

