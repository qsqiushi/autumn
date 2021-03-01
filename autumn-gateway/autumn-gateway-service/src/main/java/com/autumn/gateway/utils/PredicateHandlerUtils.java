package com.autumn.gateway.utils;

import com.autumn.gateway.bo.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @program: autumn-dev
 * @description: 过滤器
 * @author: qiushi
 * @create: 2020-12-25:09:41
 */
public class PredicateHandlerUtils {

  public static Mono<Void> error(ServerWebExchange exchange, String msg) {
    ServerHttpResponse response = exchange.getResponse();
    response.setStatusCode(HttpStatus.OK);
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
    return exchange.getResponse().writeWith(Flux.just(buffer));
  }

  public static ServerWebExchange setUserInfo2Header(
      ServerWebExchange exchange, UserInfo userInfo) {
    ServerHttpRequest serverHttpRequest =
        exchange
            .getRequest()
            .mutate()
            .header("userId", userInfo.getUserId())
            .header("userName", StringUtils.defaultIfEmpty(userInfo.getUserName(), ""))
            .header("loginName", StringUtils.defaultIfEmpty(userInfo.getLoginName(), ""))
            .header("mobile", StringUtils.defaultIfEmpty(userInfo.getMobile(), ""))
            .header(
                "role",
                StringUtils.defaultIfEmpty(
                    StringUtils.join(userInfo.getRoleCodes().toArray(), ","), ""))
            .header(
                "sysFlag", StringUtils.defaultIfEmpty(String.valueOf(userInfo.getSysFlag()), ""))
            .build();

    ServerWebExchange serverWebExchange = exchange.mutate().request(serverHttpRequest).build();
    return serverWebExchange;
  }
}
