package org.example.config;


import cn.hutool.json.JSONUtil;
import org.example.vo.response.ResponseBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.example.constants.ResultCode.NOT_LOGIN;

/**
 * @author : zhangxiaobo
 * @version : v1.0
 * @description : 一句话描述该类的功能
 * @createTime : 2023/4/17 8:03
 * @updateUser : zhangxiaobo
 * @updateTime : 2023/4/17 8:03
 * @updateRemark : 说明本次修改内容
 */
@Configuration
public class LoginUrlAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
    httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
    PrintWriter out = httpServletResponse.getWriter();
    out.write(JSONUtil.toJsonStr(ResponseBean.fail(NOT_LOGIN)));
    out.flush();
    out.close();

  }
}
