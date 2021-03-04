package com.autumn.gateway.filter;

import com.autumn.gateway.utils.ApplicationContextUtils;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @program: autumn
 * @description:
 * @author: qius
 * @create: 2021-03-03:16:45
 */
@Slf4j
//@Component
public class ResponseWithCacheGlobalFilter implements GlobalFilter, Ordered {
  /**
   * Process the Web request and (optionally) delegate to the next {@code WebFilter} through the
   * given {@link GatewayFilterChain}.
   *
   * @param exchange the current server exchange
   * @param chain provides a way to delegate to the next filter
   * @return {@code Mono<Void>} to indicate when request processing is complete
   */
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    Cache<String, String> cache = ApplicationContextUtils.getBean("strCache", Cache.class);

    String key = exchange.getRequest().getURI().toString();

    if (cache.getIfPresent(key) != null) {
      String msg = cache.getIfPresent(key);
      ServerHttpResponse response = exchange.getResponse();
      response.setStatusCode(HttpStatus.OK);
      response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
      byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
      DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
      return exchange.getResponse().writeWith(Flux.just(buffer));
    } else {
      return chain.filter(exchange);
    }
  }

  @Override
  public int getOrder() {

    // HIGHEST_PRECEDENCE = Integer.MIN_VALUE
    //从小到大的执行顺序
    // LOWEST_PRECEDENCE =Integer.MAX_VALUE

    return -3;
  }
}
