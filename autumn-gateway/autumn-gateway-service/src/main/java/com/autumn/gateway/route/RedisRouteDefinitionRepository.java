package com.autumn.gateway.route;

import com.google.gson.Gson;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: autumn-dev
 * @description: redis
 * @author: arthur
 * @create: 2020-12-23:14:52
 */
@Slf4j
@Component
public class RedisRouteDefinitionRepository implements RouteDefinitionRepository {

  public static final String GATEWAY_ROUTES = "autumn:gateway:route";
  private static Gson gson = new Gson();
  @Resource private RedisTemplate redisTemplate;

  @Override
  public Flux<RouteDefinition> getRouteDefinitions() {
    List<RouteDefinition> routeDefinitions = new ArrayList<>();
    redisTemplate.opsForHash().values(GATEWAY_ROUTES).stream()
        .forEach(
            routeDefinition -> {
              routeDefinitions.add(
                  gson.fromJson(routeDefinition.toString(), RouteDefinition.class));
            });
    return Flux.fromIterable(routeDefinitions);
  }

  @Override
  public Mono<Void> save(Mono<RouteDefinition> route) {
    return route.flatMap(
        routeDefinition -> {
          redisTemplate
              .opsForHash()
              .put(GATEWAY_ROUTES, routeDefinition.getId(), gson.toJson(routeDefinition));
          return Mono.empty();
        });
  }

  @Override
  public Mono<Void> delete(Mono<String> routeId) {
    log.info("this is  RedisRouteDefinitionRepository del ");
    return routeId.flatMap(
        id -> {
          log.info("del route id[{}]", id);
          if (redisTemplate.opsForHash().hasKey(GATEWAY_ROUTES, id)) {
            redisTemplate.opsForHash().delete(GATEWAY_ROUTES, id);
            log.info(" route id [{}] has bean deleted", id);
            return Mono.empty();
          }
          return Mono.defer(
              () -> Mono.error(new NotFoundException("RouteDefinition not found: " + routeId)));
        });
  }
}
