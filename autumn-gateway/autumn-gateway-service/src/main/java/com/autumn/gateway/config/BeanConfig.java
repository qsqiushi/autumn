package com.autumn.gateway.config;

import com.autumn.gateway.handler.predicate.AuthenticationGatewayFilterFactory;
import com.autumn.gateway.handler.predicate.OpenApiGatewayFilterFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @program: airlook-dev
 * @description:
 * @author: qius
 * @create: 2020-12-22:19:48
 */
@Configuration
public class BeanConfig {

  @Resource private HttpClientBuilder httpClientBuilder;

  @Bean
  public AuthenticationGatewayFilterFactory authenticationGatewayFilterFactory() {
    return new AuthenticationGatewayFilterFactory().setHttpClientBuilder(httpClientBuilder);
  }

  @Bean
  public OpenApiGatewayFilterFactory openApiGatewayFilterFactory() {
    return new OpenApiGatewayFilterFactory().setHttpClientBuilder(httpClientBuilder);
  }
}
