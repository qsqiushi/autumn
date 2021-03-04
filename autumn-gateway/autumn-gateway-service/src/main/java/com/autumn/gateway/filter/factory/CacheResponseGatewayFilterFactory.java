package com.autumn.gateway.filter.factory;

import com.autumn.data.redis.service.RedisService;
import com.autumn.gateway.common.GateWayConstants;
import com.autumn.gateway.utils.ApplicationContextUtils;
import com.google.common.cache.Cache;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * @program: autumn
 * @description: 缓存response过滤器工厂
 * @author: qiushi
 * @create: 2021-03-03:19:18
 */
@Slf4j
public class CacheResponseGatewayFilterFactory
    extends AbstractGatewayFilterFactory<CacheResponseGatewayFilterFactory.Config> {

  static {
    log.info("CacheResponseGatewayFilterFactory begin to work");
  }

  /** 缓存介质 */
  private static final String MEDIA = "media";

  /** 内存缓存 */
  private static final String MEDIA_MEMORY = "memory";

  /** redis作为存储介质缓存 */
  private static final String MEDIA_REDIS = "redis";

  private RedisService<String> redisService;

  private Cache<String, String> cache;

  public CacheResponseGatewayFilterFactory() {
    super(Config.class);
  }

  public CacheResponseGatewayFilterFactory setRedisService(RedisService<String> redisService) {
    this.redisService = redisService;
    return this;
  }

  public CacheResponseGatewayFilterFactory setCache(Cache<String, String> cache) {
    this.cache = cache;
    return this;
  }

  @Override
  public GatewayFilter apply(Config config) {

    return new OrderedGatewayFilter(
        new GatewayFilter() {
          @Override
          public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            ServerHttpResponseDecorator decoratedResponse =
                new ServerHttpResponseDecorator(originalResponse) {
                  @Override
                  public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    if (body instanceof Flux) {
                      Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                      return super.writeWith(
                          fluxBody.map(
                              dataBuffer -> {
                                // probably should reuse buffers
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                // 释放掉内存
                                DataBufferUtils.release(dataBuffer);
                                String result = new String(content, Charset.forName("UTF-8"));

                                if (exchange.getRequest().getMethod() == HttpMethod.GET) {

                                  if (StringUtils.equals(config.getMedia(), MEDIA_REDIS)) {
                                    // redis作为缓存介质

                                    redisService.set(
                                        exchange.getRequest().getURI().toString(), result, 60);

                                  } else {
                                    // 使用内存作为缓存介质
                                    Cache<String, String> cache =
                                        ApplicationContextUtils.getBean("strCache", Cache.class);
                                    cache.put(exchange.getRequest().getURI().toString(), result);
                                  }

                                  log.info(
                                      "Get请求[{}]返回结果是[{}]已缓存",
                                      exchange.getRequest().getURI().toString(),
                                      result);
                                }

                                byte[] uppedContent =
                                    new String(content, Charset.forName("UTF-8")).getBytes();
                                return bufferFactory.wrap(uppedContent);
                              }));
                    }
                    // if body is not a flux. never got there.
                    return super.writeWith(body);
                  }
                };
            // replace response with decorator
            return chain.filter(exchange.mutate().response(decoratedResponse).build());
          }
        },
        GateWayConstants.GATEWAY_FILTER_ORDER_CACHE_RESPONSE);
  }

  @Override
  public List<String> shortcutFieldOrder() {
    return Arrays.asList(MEDIA);
  }

  @Validated
  @Data
  @NoArgsConstructor
  @EqualsAndHashCode(callSuper = false)
  @Accessors(chain = true)
  public static class Config {

    /** 缓存介质 */
    private String media;
  }
}
