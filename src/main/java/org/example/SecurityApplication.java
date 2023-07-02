package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author : zhangxiaobo
 * @version : v1.0
 * @description : 一句话描述该类的功能
 * @createTime : 2023/6/24 14:06
 * @updateUser : zhangxiaobo
 * @updateTime : 2023/6/24 14:06
 * @updateRemark : 说明本次修改内容
 */
@SpringBootApplication
//@ComponentScan({"org.example.annotation"})
public class SecurityApplication {
  public static void main(String[] args) {
    SpringApplication.run(SecurityApplication.class, args);
  }
}
