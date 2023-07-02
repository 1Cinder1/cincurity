package org.example.vo.request;


import lombok.Data;

/**
 * @author : zhangxiaobo
 * @version : v1.0
 * @description : 一句话描述该类的功能
 * @createTime : 2023/4/17 8:03
 * @updateUser : zhangxiaobo
 * @updateTime : 2023/4/17 8:03
 * @updateRemark : 说明本次修改内容
 */
@Data
public class LoginParamsVo {
  private String username;
  private String password;
  private String captcha;
  private String smsId;
  private String grantType;
  private String clientId;
  private String clientSecret;
}
