package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.Resource;

/**
 * @author zhangxiaobo
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
  private static final String RESOURCE_ID = "all";
  @Autowired
  private TokenStore tokenStore;

  @Resource
  private LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint;
  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.resourceId(RESOURCE_ID)
            .tokenStore(tokenStore)
            .stateless(true);
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
      .antMatchers("/oauth/**").permitAll()
      .antMatchers("/test/**").permitAll()
//      .antMatchers("/**").access("#oauth2.hasScope('all')")
      .and().csrf().disable()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .exceptionHandling()
      .authenticationEntryPoint(loginUrlAuthenticationEntryPoint);
  }

}
