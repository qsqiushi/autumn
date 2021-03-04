package com.autumn.gateway.config;

import com.autumn.data.redis.service.RedisService;
import com.autumn.gateway.common.GateWayConstants;
import com.autumn.gateway.handler.predicate.AuthenticationGatewayFilterFactory;
import com.autumn.gateway.handler.predicate.CacheResponseGatewayFilterFactory;
import com.autumn.gateway.handler.predicate.OpenApiGatewayFilterFactory;
import com.autumn.gateway.handler.predicate.RespondCacheGatewayFilterFactory;
import com.google.common.cache.Cache;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

  @Resource(name = GateWayConstants.MEMORY_CACHE_BEAN_NAME)
  private Cache<String, String> cache;

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
}
