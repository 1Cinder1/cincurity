package org.example.service.user.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.SecurityApplication;
import org.example.dao.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author : zhangxiaobo
 * @version : v1.0
 * @description : password登录方式的校验
 * @createTime : 2023/4/17 8:03
 * @updateUser : zhangxiaobo
 * @updateTime : 2023/4/17 8:03
 * @updateRemark : 说明本次修改内容
 */
@Service("UserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

  @Resource
  public Environment environment;

  @Resource
  private UserMapper userMapper;

//  private String usernameColumn;
//  @Value("${dataSourceVariable.passwordColumnName}")
//  private String passwordColumn;
//  @Value("${dataSourceVariable.isbannedColumnName}")
//  private String isbannedColumn;
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (username.isEmpty()){
      throw new RuntimeException("请输入用户名");
    }
    String usernameColumn = environment.getProperty("dataSourceVariable.usernameColumnName");
    String passwordColumn = environment.getProperty("dataSourceVariable.passwordColumnName");
    String isbannedColumn = environment.getProperty("dataSourceVariable.isbannedColumnName");

    QueryWrapper<org.example.entity.User> userqueryWrapper = new QueryWrapper<>();
    userqueryWrapper.eq(usernameColumn,username);
    org.example.entity.User user = (org.example.entity.User) userMapper.selectOne(userqueryWrapper);
    if (user == null ){
      throw new RuntimeException("用户不存在");
    }
    String foundUsername;
    String foundPassword;
    byte isBanned;
    try {
       foundUsername= (String) getGetMethod(org.example.entity.User.class, usernameColumn).invoke(user);
       foundPassword = (String) getGetMethod(org.example.entity.User.class, passwordColumn).invoke(user);

       isBanned = (byte) getGetMethod(org.example.entity.User.class, isbannedColumn).invoke(user);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
    return User.withUsername(foundUsername)
      .password(foundPassword)
      .disabled(isBanned != 0x00)
      .authorities("all")
      .build();
  }

  /**
   * java反射bean的get方法
   *
   * @param objectClass objectClass
   * @param fieldName fieldName
   * @return Method
   * @throws RuntimeException
   */
  public static Method getGetMethod(Class<?> objectClass, String fieldName) {
    StringBuilder sb = new StringBuilder();
    sb.append("get");
    if (fieldName.substring(0,2).equals("is")){
      sb.append("Is").append(fieldName.substring(3, 4).toUpperCase(Locale.ROOT)).append(fieldName.substring(4));
    }else {
      sb.append(fieldName.substring(0, 1).toUpperCase(Locale.ROOT));
      sb.append(fieldName.substring(1));
    }

    try {
      return objectClass.getMethod(sb.toString());
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Reflect error!");
    }
  }
}
