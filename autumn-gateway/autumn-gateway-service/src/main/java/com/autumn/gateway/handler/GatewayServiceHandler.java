package com.autumn.gateway.handler; // package com.airlook.map.gateway;
//
// import com.airlook.gateway.route.RedisRouteDefinitionRepository;
// import com.airlook.map.gateway.common.GateWayConstants;
// import com.airlook.map.gateway.entity.Route;
// import com.airlook.map.gateway.entity.RouteGroup;
// import com.airlook.map.gateway.entity.enums.Status;
// import com.airlook.map.gateway.mapper.RouteGroupMapper;
// import com.airlook.map.gateway.mapper.RouteMapper;
// import com.alibaba.fastjson.JSON;
// import com.alibaba.fastjson.JSONArray;
// import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
// import org.springframework.cloud.gateway.filter.FilterDefinition;
// import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
// import org.springframework.cloud.gateway.route.RouteDefinition;
// import org.springframework.context.ApplicationEventPublisher;
// import org.springframework.context.ApplicationEventPublisherAware;
// import org.springframework.stereotype.Component;
// import org.springframework.util.CollectionUtils;
// import org.springframework.util.StringUtils;
// import org.springframework.web.util.UriComponentsBuilder;
// import reactor.core.publisher.Mono;
//
// import javax.annotation.Resource;
// import java.net.URI;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;
//
/// **
// * @description:
// * @author: shenyonggang
// * @date: 2020-11-19 15:40
// */
// @Slf4j
// @Component
// public class GatewayServiceHandler implements ApplicationEventPublisherAware, CommandLineRunner {
//
//  @Resource private RedisRouteDefinitionRepository routeDefinitionWriter;
//
//  private ApplicationEventPublisher publisher;
//
//  @Autowired private RouteMapper routeMapper;
//
//  @Autowired private RouteGroupMapper routeGroupMapper;
//
//  @Override
//  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
//    publisher = applicationEventPublisher;
//  }
//
//  public void loadRouteConfig() {
//
//    long startTime = System.currentTimeMillis();
//
//    // 从数据库拿到路由配置
//    List<Route> gatewayRouteList =
//        routeMapper.selectList(
//            new QueryWrapper<Route>().lambda().eq(Route::getStatus, Status.ENABLE));
//    if (CollectionUtils.isEmpty(gatewayRouteList)) {
//      log.error("网关路由初始化失败，原因：未找到路由数据");
//      return;
//    }
//
//    log.info("网关路由配置信息：{}", JSON.toJSONString(gatewayRouteList));
//
//    // 查询网关分组
//    List<RouteGroup> groups =
//        routeGroupMapper.selectList(
//            new QueryWrapper<RouteGroup>().lambda().eq(RouteGroup::getStatus, Status.ENABLE));
//    if (CollectionUtils.isEmpty(groups)) {
//      log.error("网关路由初始化失败，原因：未找到路由分组数据");
//      return;
//    }
//    log.info("网关路由分组配置信息：{}", JSON.toJSONString(groups));
//
//    Map<String, String> groupDomainMap =
//        groups.stream().collect(Collectors.toMap(RouteGroup::getGroupCode,
// RouteGroup::getDomain));
//
//    gatewayRouteList.forEach(
//        gatewayRoute -> {
//          RouteDefinition routeDefinition = new RouteDefinition();
//
//          routeDefinition.setId(gatewayRoute.getRouteId());
//          routeDefinition.setUri(handleUri(gatewayRoute.getRouteUri()));
//
//          if (gatewayRoute.getRouteOrder() != 0) {
//            routeDefinition.setOrder(gatewayRoute.getRouteOrder());
//          }
//
//          // 处理predicates，将域名融合进去
//          List<PredicateDefinition> predicateDefinitions =
//              handlePredicates(gatewayRoute, groupDomainMap);
//          routeDefinition.setPredicates(predicateDefinitions);
//
//          // 处理filters
//          List<FilterDefinition> filterDefinitions = handleFilters(gatewayRoute);
//          routeDefinition.setFilters(filterDefinitions);
//
//          routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
//        });
//    this.publisher.publishEvent(new RefreshRoutesEvent(this));
//    log.info("刷新路由信息耗时: {} ms", System.currentTimeMillis() - startTime);
//  }
//
//  /**
//   * 处理URI
//   *
//   * @param uri
//   * @return
//   */
//  private URI handleUri(String uri) {
//    if (uri.startsWith("http") || uri.startsWith("https")) {
//      return UriComponentsBuilder.fromHttpUrl(uri).build().toUri();
//    } else {
//      // uri为 lb://consumer-service 时使用下面的方法
//      return URI.create(uri);
//    }
//  }
//
//  /**
//   * @param route
//   * @param groupDomainMap
//   * @return
//   */
//  private List<PredicateDefinition> handlePredicates(
//      Route route, Map<String, String> groupDomainMap) {
//    if (GateWayConstants.EMPTY_ARRAY.equals(route.getPredicates())) {
//      log.error("路由ID={} 没有对应的predicats信息", route.getRouteId());
//      return null;
//    }
//    String domain = groupDomainMap.get(route.getRouteGroup());
//    if (StringUtils.isEmpty(domain)) {
//      log.error("路由ID={} 对应的分组={} 不存在", route.getRouteId(), route.getRouteGroup());
//      return null;
//    }
//
//    List<PredicateDefinition> predicateDefinitions =
//        JSONArray.parseArray(route.getPredicates(), PredicateDefinition.class);
//    PredicateDefinition hostPredicate = new PredicateDefinition();
//    hostPredicate.setName("Host");
//    Map<String, String> predicateParams = new HashMap<>();
//    predicateParams.put("patterns", domain);
//    hostPredicate.setArgs(predicateParams);
//    predicateDefinitions.add(hostPredicate);
//
//    return predicateDefinitions;
//  }
//
//  /**
//   * @param route
//   * @return
//   */
//  private List<FilterDefinition> handleFilters(Route route) {
//    if (GateWayConstants.EMPTY_ARRAY.equals(route.getFilters())) {
//      log.error("路由ID={} 没有对应的filters信息", route.getRouteId());
//      return null;
//    }
//
//    List<FilterDefinition> filterDefinitions =
//        JSONArray.parseArray(route.getFilters(), FilterDefinition.class);
//    return filterDefinitions;
//  }
//
//  @Override
//  public void run(String... args) throws Exception {
//    this.loadRouteConfig();
//  }
//
//  public void deleteRoute(String routeId) {
//    routeDefinitionWriter.delete(Mono.just(routeId)).subscribe();
//    this.publisher.publishEvent(new RefreshRoutesEvent(this));
//  }
// }
