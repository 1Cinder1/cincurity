package org.example.annotation;

import java.lang.annotation.*;

/**
 * @author zhangxiaobo
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface InitSecurity {

  String dataSourceUrl();
  String username ();
  String password ();
  String userTableName() ;
  String usernameColumnName() ;
  String passwordColumnName();
  String isbannedColumnName();
  String isdeletedColumnName();
  String useridColumnName();
}
