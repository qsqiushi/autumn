package com.autumn;

import com.autumn.gateway.handler.predicate.AuthenticationGatewayFilterFactory;
import com.autumn.gateway.handler.predicate.CacheResponseGatewayFilterFactory;
import com.autumn.gateway.handler.predicate.OpenApiGatewayFilterFactory;
import com.autumn.gateway.handler.predicate.RespondCacheGatewayFilterFactory;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.google.gson.Gson;
import org.springframework.cloud.gateway.handler.predicate.HeaderRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * @program:
 * @description:
 * @author: qiushi
 * @create: 2020-12-22:17:44
 */
public class MainTest {

  public static void main(String[] args) {
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

    System.out.println(NameUtils.normalizeFilterFactoryName(CacheResponseGatewayFilterFactory.class));

    System.out.println(NameUtils.normalizeFilterFactoryName(RespondCacheGatewayFilterFactory.class));


    CacheResponseGatewayFilterFactory cacheResponseGatewayFilterFactory = new CacheResponseGatewayFilterFactory();
    cacheResponseGatewayFilterFactory.setRedisService(null).setCache(null);
  }
}
