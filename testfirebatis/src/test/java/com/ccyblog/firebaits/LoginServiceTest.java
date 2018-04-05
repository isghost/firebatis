package com.ccyblog.firebaits;

import com.ccyblog.firebaits.dao.UserDao;
import com.ccyblog.firebaits.po.UserPO;
import com.ccyblog.firebaits.service.UserService;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author isghost
 * @date 2018/1/18.
 * @desc
 */
@Sql(value = "classpath:sql/myfire_user.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
public class LoginServiceTest extends BaseTest{


    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Test
    public void testAddVersion()  {
        UserPO ccy = userDao.getByUserName("ccy");
        System.out.println(ccy.getPassword());
        userDao.updateByPrimaryKey(ccy);
        UserPO ccy2 = userDao.getByUserName("ccy");
        Assert.assertEquals(1, ccy2.getVersion() - ccy.getVersion());
    }

    @Test
    public void testThrowException(){
        List<Integer> index = Arrays.asList(1, 2);
        UserPO userPO = userDao.getByUserName("userPO");
        try{
            index.parallelStream().forEach( i -> userService.updateUser(userPO));
        }catch (RuntimeException e){
            return ;
        }
        Assert.fail();
    }

    @Test
    public void testRectify(){
        List<String> nameList = Arrays.asList("ccy");
        userDao.updateVersionByNameList(nameList);
        userDao.updateVersionByNameList(Arrays.asList());
    }
}
