package com.autumn.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @program: dataearth-cloud-dev
 * @description:
 * @author: qius
 * @create: 2019-09-10:17:40
 */
@Slf4j
@Component
public class RequestBodyFilter implements GlobalFilter, Ordered {

  private static final String CACHE_REQUEST_BODY_OBJECT_KEY = "cachedRequestBodyObject";

  @Override
  @SuppressWarnings("unchecked")
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    Class inClass = String.class;

    ServerRequest serverRequest =
        new DefaultServerRequest(exchange, HandlerStrategies.withDefaults().messageReaders());

    ServerHttpRequest request = exchange.getRequest();

    // 只记录 http 请求(包含 https)
    String schema = request.getURI().getScheme();
    if ((!"http".equals(schema) && !"https".equals(schema))) {
      return chain.filter(exchange);
    }

    String contentType = request.getHeaders().getFirst("Content-Type");
    String upload = request.getHeaders().getFirst("upload");

    // 没有内容类型不读取body
    if (StringUtils.isEmpty(contentType)) {
      return chain.filter(exchange);
    }

    // 文件上传不读取body
    if ("true".equals(upload)) {
      return chain.filter(exchange);
    }

    Mono<?> modifiedBody =
        serverRequest
            .bodyToMono(inClass)
            .flatMap(
                o -> {
                  log.debug("requestBody:{}", o);
                  exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY, o);
                  return Mono.justOrEmpty(o);
                });

    BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, inClass);
    HttpHeaders headers = new HttpHeaders();
    headers.putAll(exchange.getRequest().getHeaders());

    CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
    return bodyInserter
        .insert(outputMessage, new BodyInserterContext())
        .then(
            Mono.defer(
                () -> {
                  ServerHttpRequestDecorator decorator =
                      new ServerHttpRequestDecorator(exchange.getRequest()) {

                        @Override
                        public HttpHeaders getHeaders() {
                          long contentLength = headers.getContentLength();
                          HttpHeaders httpHeaders = new HttpHeaders();
                          httpHeaders.putAll(super.getHeaders());
                          if (contentLength > 0) {
                            httpHeaders.setContentLength(contentLength);
                          } else {
                            // TODO: this causes a 'HTTP/1.1 411 Length Required' on httpbin.org
                            httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                          }
                          return httpHeaders;
                        }

                        @Override
                        public Flux<DataBuffer> getBody() {
                          return outputMessage.getBody();
                        }
                      };
                  return chain.filter(exchange.mutate().request(decorator).build());
                }));
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 1;
  }
}
