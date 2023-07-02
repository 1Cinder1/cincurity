package org.example.annotation;

import org.example.SecurityApplication;
import org.example.decoupleConfig.DecoupleConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * @author : zhangxiaobo
 * @version : v1.0
 * @description : 一句话描述该类的功能
 * @createTime : 2023/6/26 14:33
 * @updateUser : zhangxiaobo
 * @updateTime : 2023/6/26 14:33
 * @updateRemark : 说明本次修改内容
 */
@Component
public class EnableAnotherClassPostProcessor implements BeanPostProcessor {
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean.getClass().isAnnotationPresent(InitSecurity.class)){
      InitSecurity annotation = bean.getClass().getAnnotation(InitSecurity.class);
      String dataSourceUrl = annotation.dataSourceUrl();
      String username = annotation.username();
      String password = annotation.password();
      String userTableName = annotation.userTableName();
      String usernameColumnName = annotation.usernameColumnName();
      String passwordColumnName = annotation.passwordColumnName();
      String isbannedColumnName = annotation.isbannedColumnName();
      String isdeletedColumnName = annotation.isdeletedColumnName();
      String useridColumnName = annotation.useridColumnName();
      DecoupleConfig decoupleConfig = new DecoupleConfig(dataSourceUrl,username,password,userTableName,
        usernameColumnName,passwordColumnName,isbannedColumnName,isdeletedColumnName,useridColumnName
      );
      decoupleConfig.init();
    }
    return bean;
  }
}
