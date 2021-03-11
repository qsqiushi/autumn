package com.autumn.gateway.config;

import com.autumn.data.redis.service.RedisService;
import com.autumn.gateway.common.GateWayConstants;
import com.autumn.gateway.filter.factory.*;
import com.google.common.cache.Cache;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.DispatcherHandler;

import javax.annotation.Resource;

/**
 * @program: autumn-dev
 * @description:
 * @author: qiushi
 * @create: 2020-12-22:19:48
 */
@Configuration
public class BeanConfig {

  @Resource private HttpClientBuilder httpClientBuilder;

  @Resource private RedisService<String> redisService;
  @Resource private LoadBalancerClientFactory clientFactory;

  @Resource(name = GateWayConstants.MEMORY_CACHE_BEAN_NAME)
  private Cache<String, String> cache;

  @Resource private ObjectProvider<DispatcherHandler> dispatcherHandler;

  @Bean
  public AuthenticationGatewayFilterFactory authenticationGatewayFilterFactory() {
    return new AuthenticationGatewayFilterFactory().setHttpClientBuilder(httpClientBuilder);
  }

  @Bean
  public OpenApiGatewayFilterFactory openApiGatewayFilterFactory() {
    return new OpenApiGatewayFilterFactory().setHttpClientBuilder(httpClientBuilder);
  }

  @Bean
  public CacheResponseGatewayFilterFactory cacheResponseGatewayFilterFactory() {
    return new CacheResponseGatewayFilterFactory().setCache(cache).setRedisService(redisService);
  }

  @Bean
  public RespondCacheGatewayFilterFactory respondCacheGatewayFilterFactory() {
    return new RespondCacheGatewayFilterFactory().setCache(cache).setRedisService(redisService);
  }

  @Bean
  public SpecialHystrixGatewayFilterFactory specialHystrixGatewayFilterFactory() {
    return new SpecialHystrixGatewayFilterFactory(dispatcherHandler);
  }

  @Bean
  public GrayReactiveLoadBalancerClientGatewayFilterFactory
      grayReactiveLoadBalancerClientGatewayFilterFactory() {
    return new GrayReactiveLoadBalancerClientGatewayFilterFactory().setClientFactory(clientFactory);
  }
}
