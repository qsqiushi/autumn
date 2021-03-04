package com.autumn.gateway.filter;

import com.autumn.gateway.utils.ApplicationContextUtils;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * @program: autumn
 * @description:
 * @author: qiushi
 * @create: 2021-03-02:19:44
 */
@Slf4j
//@Component
public class WrapperResponseGlobalFilter implements GlobalFilter, Ordered {
  @Override
  public int getOrder() {
    // -1 is response write filter, must be called before that
    return -2;
  }

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
                        String s = new String(content, Charset.forName("UTF-8"));

                        if (exchange.getRequest().getMethod() == HttpMethod.GET) {
                          Cache<String, String> cache =
                              ApplicationContextUtils.getBean("strCache", Cache.class);
                          cache.put(exchange.getRequest().getURI().toString(), s);
                        }

                        log.info("返回结果是[{}]", s);
                        // TODO，s就是response的值，想修改、查看就随意而为了
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
}
