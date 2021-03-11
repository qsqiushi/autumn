package com.autumn.gateway.filter.factory;

import com.autumn.gateway.common.GateWayConstants;
import com.autumn.gateway.loadbalancer.GrayLoadBalancer;
import com.autumn.gateway.loadbalancer.model.Strategy;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.reactive.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * @program: autumn
 * @description: 灰度再平衡过滤器工厂
 * @author: qiushi
 * @create: 2021-03-09:19:26
 */
@Slf4j
public class GrayReactiveLoadBalancerClientGatewayFilterFactory
    extends AbstractGatewayFilterFactory<
        GrayReactiveLoadBalancerClientGatewayFilterFactory.Config> {

  private LoadBalancerClientFactory clientFactory;

  public GrayReactiveLoadBalancerClientGatewayFilterFactory setClientFactory(
      LoadBalancerClientFactory clientFactory) {
    this.clientFactory = clientFactory;
    return this;
  }

  public GrayReactiveLoadBalancerClientGatewayFilterFactory() {
    super(GrayReactiveLoadBalancerClientGatewayFilterFactory.Config.class);
  }

  /** 负载均衡方式 */
  private static final String MODE = "mode";

  private static final String STRATEGIES = "strategies";

  @Override
  public GatewayFilter apply(Config config) {

    return new OrderedGatewayFilter(
        new GatewayFilter() {
          @Override
          public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

            URI url = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
            String schemePrefix =
                exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR);

            if (url == null) {
              return chain.filter(exchange);
            }

            if (GateWayConstants.SCHEME_PREFIX.equals(url.getScheme())
                || GateWayConstants.SCHEME_PREFIX.equals(schemePrefix)) {
              // 新增原始请求路径
              ServerWebExchangeUtils.addOriginalRequestUrl(exchange, url);

              Strategy strategy = new Strategy(config.getMode(), config.getStrategies());

              if (strategy.getLoadBalancerEnum() == null
                  || CollectionUtils.isEmpty(strategy.getParams())) {

                return chain.filter(exchange);
              }

              return choose(exchange, strategy)
                  .doOnNext(
                      (response) -> {
                        if (!response.hasServer()) {
                          throw NotFoundException.create(
                              Boolean.TRUE, "Unable to find instance for " + url.getHost());
                        } else {
                          URI uri = exchange.getRequest().getURI();
                          String overrideScheme = null;
                          if (schemePrefix != null) {
                            overrideScheme = url.getScheme();
                          }

                          DelegatingServiceInstance serviceInstance =
                              new DelegatingServiceInstance(
                                  (ServiceInstance) response.getServer(), overrideScheme);
                          URI requestUrl = reconstructURI(serviceInstance, uri);

                          log.info("LoadBalancerClientFilter url chosen: " + requestUrl);

                          exchange
                              .getAttributes()
                              .put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);
                        }
                      })
                  .then(chain.filter(exchange));
            }
            return chain.filter(exchange);
          }
        },
        GateWayConstants.GATEWAY_FILTER_ORDER_GRAY_REACTIVE_LOAD_BALANCER);
  }

  @Override
  public List<String> shortcutFieldOrder() {
    return Arrays.asList(MODE, STRATEGIES);
  }

  @Validated
  @Data
  @NoArgsConstructor
  @EqualsAndHashCode(callSuper = false)
  @Accessors(chain = true)
  public static class Config {

    /** 限流模式 */
    private String mode;

    /** 限流策略 */
    private List<String> strategies;
  }

  private Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange, Strategy strategy) {
    URI uri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
    GrayLoadBalancer loadBalancer =
        new GrayLoadBalancer(
            // 获得服务实例
            clientFactory.getLazyProvider(uri.getHost(), ServiceInstanceListSupplier.class),
            uri.getHost(),
            strategy);
    if (loadBalancer == null) {
      throw new NotFoundException("No loadbalancer available for " + uri.getHost());
    } else {
      return loadBalancer.choose(this.createRequest(exchange));
    }
  }

  private Request createRequest(ServerWebExchange exchange) {
    HttpHeaders headers = exchange.getRequest().getHeaders();
    Request<HttpHeaders> request = new DefaultRequest<>(headers);
    return request;
  }

  private URI reconstructURI(ServiceInstance serviceInstance, URI original) {
    return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
  }
}
