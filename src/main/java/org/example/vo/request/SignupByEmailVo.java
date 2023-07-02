package org.example.vo.request;


import lombok.Data;

/**
 * @author : zhangxiaobo
 * @version : v1.0
 * @description : 请求的返回Json对应的Bean的模板类
 * @createTime : 2023/4/17 8:03
 * @updateUser : zhangxiaobo
 * @updateTime : 2023/4/17 8:03
 * @updateRemark :
 */
@Data
public class SignupByEmailVo {
  private String phone;
  private String captcha;
  private String password;
  private String smsId;
}
