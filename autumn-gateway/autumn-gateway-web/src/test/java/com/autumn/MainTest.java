package com.autumn;

import com.autumn.gateway.filter.factory.*;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.cloud.gateway.handler.predicate.HeaderRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.support.NameUtils;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @program:
 * @description:
 * @author: qiushi
 * @create: 2020-12-22:17:44
 */
public class MainTest {

  public static void main(String[] args) {

    Mono.create(
        new Consumer<MonoSink<Void>>() {
          @Override
          public void accept(MonoSink<Void> monoSink) {}
        });

    Map<String, String> versionMap = new HashMap<>();
    versionMap.put("version", "versionNo");
    final Set<Map.Entry<String, String>> attributes =
        Collections.unmodifiableSet(versionMap.entrySet());

    System.out.println(
        NameUtils.normalizeFilterFactoryName(SpecialHystrixGatewayFilterFactory.class));

    System.out.println(
        NameUtils.normalizeFilterFactoryName(AuthenticationGatewayFilterFactory.class));

    System.out.println(NameUtils.normalizeRoutePredicateName(HeaderRoutePredicateFactory.class));

    System.out.println(StandardCharsets.UTF_8.displayName());

    System.out.println(IdWorker.getIdStr());
    System.out.println(IdWorker.getIdStr());
    System.out.println(IdWorker.getIdStr());
    System.out.println(NameUtils.normalizeRoutePredicateName(PathRoutePredicateFactory.class));

    System.out.println(NameUtils.generateName(0));

    System.out.println(NameUtils.generateName(1));

    System.out.println(NameUtils.generateName(2));

    System.out.println(NameUtils.normalizeFilterFactoryName(OpenApiGatewayFilterFactory.class));

    System.out.println(
        NameUtils.normalizeFilterFactoryName(CacheResponseGatewayFilterFactory.class));

    System.out.println(
        NameUtils.normalizeFilterFactoryName(RespondCacheGatewayFilterFactory.class));

    CacheResponseGatewayFilterFactory cacheResponseGatewayFilterFactory =
        new CacheResponseGatewayFilterFactory();
    cacheResponseGatewayFilterFactory.setRedisService(null).setCache(null);

    System.out.println(
        NameUtils.normalizeFilterFactoryName(
            GrayReactiveLoadBalancerClientGatewayFilterFactory.class));
  }
}
