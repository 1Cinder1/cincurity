package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.annotation.Resource;
import javax.sql.DataSource;


/**
 * @author : zhangxiaobo
 * @version : v1.0
 * @description : 一句话描述该类的功能
 * @createTime : 2023/4/17 8:03
 * @updateUser : zhangxiaobo
 * @updateTime : 2023/4/17 8:03
 * @updateRemark :
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

  @Resource
  private TokenStore tokenStore;

  @Resource
  private ClientDetailsService clientDetailsService;

  @Resource
  private AuthorizationCodeServices authorizationCodeServices;
  @Resource
  private AuthenticationManager authenticationManager;

  @Resource
  private UserDetailsService userDetailsService;

  @Resource
  private PasswordEncoder passwordEncoder;
  @Bean
  public AuthorizationServerTokenServices tokenServices(){
    DefaultTokenServices services = new DefaultTokenServices();
    services.setClientDetailsService(clientDetailsService);
    services.setTokenStore(tokenStore);
    services.setSupportRefreshToken(true);
    services.setReuseRefreshToken(true);
    services.setAccessTokenValiditySeconds(7200);//改成 2个小时
    services.setRefreshTokenValiditySeconds(259200);//TODO 改成24个小时
    return services;
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    security.tokenKeyAccess("permitAll()")
            .checkTokenAccess("permitAll()")
            .allowFormAuthenticationForClients();
  }

  @Bean
  public ClientDetailsService clientDetailsService(DataSource dataSource){
    ClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
    ((JdbcClientDetailsService) clientDetailsService).setPasswordEncoder(passwordEncoder);
    return clientDetailsService;
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.withClientDetails(clientDetailsService);
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    endpoints
            //存储token到数据库
            .tokenStore(tokenStore)
            .authenticationManager(authenticationManager)
            .authorizationCodeServices(authorizationCodeServices)
            .tokenServices(tokenServices())
            .userDetailsService(userDetailsService)
            .allowedTokenEndpointRequestMethods(HttpMethod.POST);
  }

  @Bean
  public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
    return new JdbcAuthorizationCodeServices(dataSource);
  }
}
