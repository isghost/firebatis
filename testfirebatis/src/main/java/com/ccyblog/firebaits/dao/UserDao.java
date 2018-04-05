package com.ccyblog.firebaits.dao;

import com.ccyblog.mybatis.annotation.EnableLock;
import com.ccyblog.firebaits.po.UserPO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {

    int deleteByPrimaryKey(Long id);

    int insert(UserPO record);

    int insertSelective(UserPO record);

    UserPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserPO record);

    @EnableLock(enableException = true)
    int updateByPrimaryKey(UserPO record);

    UserPO getByUserName(@Param("username") String username);

    int updateVersionByNameList(@Param("nameList") List<String> names);
}