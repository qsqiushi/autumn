package com.autumn.gateway.filter.factory;

import com.autumn.gateway.common.GateWayConstants;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Subscription;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.containsEncodedParts;

/**
 * @program: autumn
 * @description: 特殊熔断过滤器工厂
 * @author: qiushi
 * @create: 2021-03-04:15:32
 */
@Slf4j
public class SpecialHystrixGatewayFilterFactory
    extends AbstractGatewayFilterFactory<SpecialHystrixGatewayFilterFactory.Config> {

  private static final String FALL_BACK_URI = "fallbackUri";

  private static final String TIMEOUT = "timeout";

  /** 默认超时时间 */
  private static final Integer TIMEOUT_MILLISECOND = 2000;

  private final ObjectProvider<DispatcherHandler> dispatcherHandler;

  public SpecialHystrixGatewayFilterFactory(ObjectProvider<DispatcherHandler> dispatcherHandler) {
    super(Config.class);
    this.dispatcherHandler = dispatcherHandler;
  }

  @Override
  public GatewayFilter apply(Config config) {

    return new OrderedGatewayFilter(
        new GatewayFilter() {
          @SneakyThrows
          @Override
          public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpRequest request = exchange.getRequest();
            Integer timeout = config.getTimeout();
            if (timeout == null) {
              timeout = TIMEOUT_MILLISECOND;
            }
            String path = request.getURI().toString();

            SpecialRouteHystrixCommand command =
                new SpecialRouteHystrixCommand(
                    new URI(config.getFallbackUri()), exchange, chain, timeout, path);

            return Mono.create(
                    monoSink -> {
                      Subscription sub =
                          command
                              .toObservable()
                              .subscribe(monoSink::success, monoSink::error, monoSink::success);
                      monoSink.onCancel(sub::unsubscribe);
                    })
                .onErrorResume(
                    (Function<Throwable, Mono<Void>>)
                        throwable -> {
                          if (throwable instanceof HystrixRuntimeException) {
                            HystrixRuntimeException e = (HystrixRuntimeException) throwable;
                            HystrixRuntimeException.FailureType failureType = e.getFailureType();
                            switch (failureType) {
                              case TIMEOUT:
                                return Mono.error(new TimeoutException());
                              case COMMAND_EXCEPTION:
                                {
                                  Throwable cause = e.getCause();
                                  if (cause instanceof ResponseStatusException
                                      || AnnotatedElementUtils.findMergedAnnotation(
                                              cause.getClass(), ResponseStatus.class)
                                          != null) {
                                    return Mono.error(cause);
                                  }
                                }
                              default:
                                break;
                            }
                          }
                          return Mono.error(throwable);
                        })
                /**
                 * Return a {@code Mono<Void>} which only replays complete and error signals from
                 * this {@link Mono}.*
                 */
                .then();
          }
        },
        GateWayConstants.GATEWAY_FILTER_ORDER_HYSTRIX);
  }

  @Override
  public List<String> shortcutFieldOrder() {
    return Arrays.asList(FALL_BACK_URI, TIMEOUT);
  }

  @Validated
  @Data
  @NoArgsConstructor
  @EqualsAndHashCode(callSuper = false)
  @Accessors(chain = true)
  public static class Config {
    private String fallbackUri;
    /** timeout ms */
    private Integer timeout;
  }

  private class SpecialRouteHystrixCommand extends HystrixObservableCommand<Void> {

    private final URI fallbackUri;
    private final ServerWebExchange exchange;
    private final GatewayFilterChain chain;

    /**
     * HystrixObservableCommand使用示例 只需要继承HystrixObservableCommand类，并覆写construct方法即可
     *
     * @author qiushi
     */
    public SpecialRouteHystrixCommand(
        URI fallbackUri, ServerWebExchange exchange, GatewayFilterChain chain, String key) {
      super(
          Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(key))
              .andCommandKey(HystrixCommandKey.Factory.asKey(key)));
      this.fallbackUri = fallbackUri;
      this.exchange = exchange;
      this.chain = chain;
    }

    public SpecialRouteHystrixCommand(
        URI fallbackUri,
        ServerWebExchange exchange,
        GatewayFilterChain chain,
        int timeout,
        String key) {
      // ***出现通配符的情况**//
      super(
          Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(key))
              .andCommandKey(HystrixCommandKey.Factory.asKey(key))
              .andCommandPropertiesDefaults(
                  // 执行超时时间
                  HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(timeout)));
      this.fallbackUri = fallbackUri;
      this.exchange = exchange;
      this.chain = chain;
    }

    @Override
    protected Observable<Void> construct() {
      log.info("熔断构造线程正在执行");
      // 将流作为RxJava可观察对象
      return RxReactiveStreams.toObservable(this.chain.filter(exchange));
    }

    /**
     * If {@link #observe()} or {@link #toObservable()} fails in any way then this method will be
     * invoked to provide an opportunity to return a fallback response.
     *
     * <p>如果失败，则此方法将被禁用,禁用的目的是 提供返回回退响应的机会。
     *
     * <p>This should do work that does not require network transport to produce.
     *
     * <p>In other words, this should be a static or cached result that can immediately be returned
     * upon failure.
     *
     * <p>If network traffic is wanted for fallback (such as going to MemCache) then the fallback
     * implementation should invoke another {@link HystrixObservableCommand} instance that protects
     * against that network access and possibly has another level of fallback that does not involve
     * network access.
     *
     * <p>DEFAULT BEHAVIOR: It throws UnsupportedOperationException.
     *
     * @return R or UnsupportedOperationException if not implemented
     */
    @Override
    protected Observable<Void> resumeWithFallback() {
      if (null == fallbackUri) {
        return super.resumeWithFallback();
      }
      URI uri = exchange.getRequest().getURI();
      boolean encoded = containsEncodedParts(uri);
      URI requestUrl =
          UriComponentsBuilder.fromUri(uri)
              .host(null)
              .port(null)
              .uri(this.fallbackUri)
              .build(encoded)
              .toUri();
      exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);

      ServerHttpRequest request = this.exchange.getRequest().mutate().uri(requestUrl).build();
      ServerWebExchange mutated = exchange.mutate().request(request).build();
      DispatcherHandler dispatcherHandler =
          SpecialHystrixGatewayFilterFactory.this.dispatcherHandler.getIfAvailable();
      return RxReactiveStreams.toObservable(dispatcherHandler.handle(mutated));
    }
  }
}
