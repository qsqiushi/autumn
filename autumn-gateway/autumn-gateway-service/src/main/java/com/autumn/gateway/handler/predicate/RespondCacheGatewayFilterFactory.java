package com.autumn.gateway.handler.predicate;

import com.autumn.data.redis.service.RedisService;
import com.autumn.gateway.common.GateWayConstants;
import com.google.common.cache.Cache;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @program: autumn
 * @description: 响应缓存过滤器工厂
 * @author: qius
 * @create: 2021-03-03:19:53
 */
@Slf4j
public class RespondCacheGatewayFilterFactory
    extends AbstractGatewayFilterFactory<RespondCacheGatewayFilterFactory.Config> {

  private RedisService<String> redisService;

  private Cache<String, String> cache;

  public RespondCacheGatewayFilterFactory() {
    super(RespondCacheGatewayFilterFactory.Config.class);
  }

  public RespondCacheGatewayFilterFactory setRedisService(RedisService<String> redisService) {
    this.redisService = redisService;
    return this;
  }

  public RespondCacheGatewayFilterFactory setCache(Cache<String, String> cache) {
    this.cache = cache;
    return this;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return new OrderedGatewayFilter(
        new GatewayFilter() {
          @Override
          public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

            String key = exchange.getRequest().getURI().toString();

            String result = cache.getIfPresent(key);

            if (StringUtils.isEmpty(result)) {
              result = redisService.get(key);
            }

            if (StringUtils.isNotEmpty(result)) {
              ServerHttpResponse response = exchange.getResponse();
              response.setStatusCode(HttpStatus.OK);
              response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
              byte[] bytes = result.getBytes(StandardCharsets.UTF_8);
              DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
              return exchange.getResponse().writeWith(Flux.just(buffer));
            } else {
              return chain.filter(exchange);
            }
          }
        },
        GateWayConstants.GATEWAY_FILTER_ORDER_RESPOND_CACHE);
  }

  @Validated
  @Data
  @NoArgsConstructor
  @EqualsAndHashCode(callSuper = false)
  @Accessors(chain = true)
  public static class Config {}
}
