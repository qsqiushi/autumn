package com.autumn.gateway.service.impl;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.cluster.ClusterStateManager;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientAssignConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfigManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowClusterConfig;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.autumn.gateway.entity.Config;
import com.autumn.gateway.entity.ResourceConfig;
import com.autumn.gateway.entity.ResourceConfigParam;
import com.autumn.gateway.entity.ResourceGroup;
import com.autumn.gateway.enums.BaseStatus;
import com.autumn.gateway.enums.BaseWhether;
import com.autumn.gateway.handler.predicate.AuthenticationGatewayFilterFactory;
import com.autumn.gateway.mapper.*;
import com.autumn.gateway.po.ResourceConfigInfo;
import com.autumn.gateway.po.ResourceConfigParamPO;
import com.autumn.gateway.route.RedisRouteDefinitionRepository;
import com.autumn.gateway.service.IDynamicRouteService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.factory.HystrixGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.StripPrefixGatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.HeaderRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.HostRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 动态更新路由网关service 1）实现一个Spring提供的事件推送接口ApplicationEventPublisherAware
 * 2）提供动态路由的基础方法，可通过获取bean操作该类的方法。该类提供新增路由、更新路由、删除路由，然后实现发布的功能。
 *
 * @author
 */
@Slf4j
@Service
public class DynamicRouteServiceImpl
    implements ApplicationEventPublisherAware, IDynamicRouteService {

  @Resource private RedisRouteDefinitionRepository routeDefinitionWriter;

  @Resource private ResourceMapper resourceMapper;

  @Resource private ResourceGroupMapper groupMapper;

  @Resource private ConfigMapper configMapper;

  @Resource private ResourceConfigMapper resourceConfigMapper;

  @Resource private ResourceConfigParamMapper resourceConfigParamMapper;

  /** 发布事件 */
  private ApplicationEventPublisher publisher;

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.publisher = applicationEventPublisher;
  }

  /**
   * 删除路由
   *
   * @param id
   * @return
   */
  @Override
  public String delete(String id) {
    try {
      log.info("gateway delete route id {}", id);
      this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
      this.publisher.publishEvent(new RefreshRoutesEvent(this));
      return "delete success";
    } catch (Exception e) {
      return "delete fail";
    }
  }

  /**
   * 更新路由
   *
   * @param definition
   * @return
   */
  public String update(RouteDefinition definition) {
    try {
      log.info("gateway update route {}", definition);
      this.routeDefinitionWriter.delete(Mono.just(definition.getId())).subscribe();
    } catch (Exception e) {
      return "update fail,not find route  routeId: " + definition.getId();
    }
    try {
      routeDefinitionWriter.save(Mono.just(definition)).subscribe();
      this.publisher.publishEvent(new RefreshRoutesEvent(this));
      return "success";
    } catch (Exception e) {
      return "update route fail";
    }
  }

  /**
   * 增加路由
   *
   * @param definition
   * @return
   */
  public String add(RouteDefinition definition) {
    log.info("gateway add route {}", definition);
    routeDefinitionWriter.save(Mono.just(definition)).subscribe();
    this.publisher.publishEvent(new RefreshRoutesEvent(this));
    return "success";
  }

  @PostConstruct
  public void routeInit() {

    List<com.autumn.gateway.entity.Resource> resourceList =
        resourceMapper.selectList(
            new QueryWrapper<com.autumn.gateway.entity.Resource>()
                .lambda()
                .eq(com.autumn.gateway.entity.Resource::getDeleted, BaseWhether.NO)
                .eq(com.autumn.gateway.entity.Resource::getStatus, BaseStatus.VALID));

    Set<ApiDefinition> apiDefinitions = new HashSet<>();
    Set<GatewayFlowRule> gatewayFlowRules = new HashSet<>();
    List<DegradeRule> degradeRules = new ArrayList<>();
    List<AuthorityRule> authorityRules = new ArrayList<>();

    for (com.autumn.gateway.entity.Resource resource : resourceList) {

      RouteDefinition routeDefinition = new RouteDefinition();
      routeDefinition.setId(resource.getId());
      URI uri = UriComponentsBuilder.fromUriString(resource.getUri()).build().toUri();
      routeDefinition.setUri(uri);
      routeDefinition.setPredicates(new ArrayList<>());
      routeDefinition.setFilters(new ArrayList<>());

      if (resource.getGroupId() != null) {
        ResourceGroup group = groupMapper.selectById(resource.getGroupId());
        // 域名过滤
        PredicateDefinition headerPredicate = new PredicateDefinition();
        headerPredicate.setName(
            NameUtils.normalizeRoutePredicateName(HeaderRoutePredicateFactory.class));
        headerPredicate.setArgs(new HashMap<>());
        headerPredicate.getArgs().put(HeaderRoutePredicateFactory.HEADER_KEY, "Host");
        headerPredicate.getArgs().put(HeaderRoutePredicateFactory.REGEXP_KEY, group.getDomain());
        routeDefinition.getPredicates().add(headerPredicate);
      }

      ApiDefinition api = new ApiDefinition(resource.getId());
      api.setPredicateItems(new HashSet<>());

      // 路径过滤
      PredicateDefinition pathPredicateDefinition = new PredicateDefinition();
      pathPredicateDefinition.setName(
          NameUtils.normalizeRoutePredicateName(PathRoutePredicateFactory.class));
      pathPredicateDefinition.setArgs(new HashMap<>());

      if (resource.getUrl().contains(",")) {
        String[] urls = resource.getUrl().split(",");
        for (int i = 0; i < urls.length; i++) {
          pathPredicateDefinition.addArg(NameUtils.generateName(i), urls[i]);

          if (urls[i].endsWith("**")) {
            api.getPredicateItems()
                .add(
                    new ApiPathPredicateItem()
                        .setPattern(urls[i])
                        .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
          } else {
            api.getPredicateItems().add(new ApiPathPredicateItem().setPattern(urls[i]));
          }
        }

      } else {
        pathPredicateDefinition
            .getArgs()
            .put(PathRoutePredicateFactory.PATTERN_KEY, resource.getUrl());

        if (resource.getUrl().endsWith("**")) {
          api.getPredicateItems()
              .add(
                  new ApiPathPredicateItem()
                      .setPattern(resource.getUrl())
                      .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
        } else {
          api.getPredicateItems().add(new ApiPathPredicateItem().setPattern(resource.getUrl()));
        }
      }

      apiDefinitions.add(api);

      routeDefinition.getPredicates().add(pathPredicateDefinition);

      List<ResourceConfigInfo> resourceConfigInfoList =
          resourceConfigMapper.selectResourceConfigInfo(
              new QueryWrapper<ResourceConfig>()
                  .lambda()
                  .eq(ResourceConfig::getResourceId, resource.getId())
                  .eq(ResourceConfig::getDeleted, BaseWhether.NO)
                  .eq(ResourceConfig::getStatus, BaseStatus.VALID));

      for (ResourceConfigInfo resourceConfigInfo : resourceConfigInfoList) {
        Config config = resourceConfigInfo.getConfig();

        List<ResourceConfigParamPO> resourceConfigParamList =
            resourceConfigParamMapper.selectResourceConfigParamInfo(
                new QueryWrapper<ResourceConfigParam>()
                    .lambda()
                    .eq(ResourceConfigParam::getResourceConfigId, resourceConfigInfo.getId())
                    .eq(ResourceConfigParam::getDeleted, BaseWhether.NO)
                    .eq(ResourceConfigParam::getStatus, BaseStatus.VALID));

        switch (resourceConfigInfo.getConfig().getType()) {
          case FILTER:
            FilterDefinition filterDefinition = new FilterDefinition();
            filterDefinition.setName(config.getCode());
            filterDefinition.setArgs(new HashMap<>());
            for (ResourceConfigParamPO resourceConfigParamPO : resourceConfigParamList) {
              filterDefinition
                  .getArgs()
                  .put(
                      resourceConfigParamPO.getConfigParam().getCode(),
                      resourceConfigParamPO.getValue());
            }
            routeDefinition.getFilters().add(filterDefinition);
            break;
          case PREDICATE:
            PredicateDefinition predicateDefinition = new PredicateDefinition();
            predicateDefinition.setName(config.getName());
            predicateDefinition.setArgs(new HashMap<>());
            for (ResourceConfigParamPO resourceConfigParamPO : resourceConfigParamList) {
              predicateDefinition
                  .getArgs()
                  .put(
                      resourceConfigParamPO.getConfigParam().getCode(),
                      resourceConfigParamPO.getValue());
            }
            routeDefinition.getPredicates().add(pathPredicateDefinition);
            break;
          case GATEWAY_FLOW_RULE:
            Map<String, String> map =
                resourceConfigParamList.stream()
                    .collect(
                        Collectors.toMap(e -> e.getConfigParam().getCode(), e -> e.getValue()));

            GatewayFlowRule gatewayFlowRule =
                new GatewayFlowRule(resource.getId())
                    // 限流的阈值  2线程或者2qps
                    .setCount(map.get("count") == null ? 10 : Integer.parseInt(map.get("count")))
                    // 限流阈值类型，qps 或线程数
                    .setGrade(
                        map.get("grade") == null
                            ? RuleConstant.FLOW_GRADE_QPS
                            : Integer.parseInt(map.get("grade")))
                    // intervalSec：统计时间窗口，单位是秒，默认是 1 秒 目前仅对参数限流生效。
                    .setIntervalSec(
                        map.get("intervalSec") == null
                            ? 1
                            : Integer.parseInt(map.get("intervalSec")))
                    // 应对突发请求时额外允许的请求数目
                    .setBurst(map.get("burst") == null ? 1 : Integer.parseInt(map.get("burst")))
                    // 流量整形的控制效果，同限流规则的 controlBehavior 字段，目前支持快速失败和匀速排队两种模式，默认是快速失败。
                    .setControlBehavior(
                        map.get("controlBehavior") == null
                            ? RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER
                            : Integer.parseInt(map.get("controlBehavior")))
                    // 匀速排队模式下的最长排队时间，单位是毫秒，仅在匀速排队模式下生效。
                    .setMaxQueueingTimeoutMs(
                        map.get("maxQueueingTimeoutMs") == null
                            ? 600
                            : Integer.parseInt(map.get("maxQueueingTimeoutMs")));

            if (map.get("paramItem") != null) {
              // paramItem 参数限流配置。若不提供，则代表不针对参数进行限流，该网关规则将会被转换成普通流控规则；否则会转换成热点规则。其中的字段：
              switch (Integer.parseInt(map.get("paramItem"))) {
                case SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP:
                  gatewayFlowRule.setParamItem(
                      new GatewayParamFlowItem()
                          .setParseStrategy(
                              SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP));
                  break;
                case SentinelGatewayConstants.PARAM_PARSE_STRATEGY_HOST:
                  gatewayFlowRule.setParamItem(
                      new GatewayParamFlowItem()
                          .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_HOST));
                  break;

                case SentinelGatewayConstants.PARAM_PARSE_STRATEGY_HEADER:
                  // 从请求中提取参数的策略，目前支持提取来源 IP
                  // （PARAM_PARSE_STRATEGY_CLIENT_IP）、Host（PARAM_PARSE_STRATEGY_HOST）、任意
                  // Header（PARAM_PARSE_STRATEGY_HEADER）和任意 URL
                  // 参数（PARAM_PARSE_STRATEGY_URL_PARAM）四种模式。
                  // fieldName：若提取策略选择 Header 模式或 URL 参数模式，则需要指定对应的 header 名称或 URL 参数名称。
                  // pattern 和 matchStrategy：为后续参数匹配特性预留，目前未实现。
                  gatewayFlowRule.setParamItem(
                      new GatewayParamFlowItem()
                          .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_HEADER)
                          .setFieldName(map.get("fieldName")));
                  break;

                case SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM:
                  gatewayFlowRule.setParamItem(
                      new GatewayParamFlowItem()
                          .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
                          .setFieldName(map.get("fieldName")));
                  break;
                default:
                  break;
              }
            }

            gatewayFlowRules.add(gatewayFlowRule);
            break;
          case DEGRADE_RULE:
            Map<String, String> degradeRuleParamMap =
                resourceConfigParamList.stream()
                    .collect(
                        Collectors.toMap(e -> e.getConfigParam().getCode(), e -> e.getValue()));

            DegradeRule degradeRule = new DegradeRule();
            // 资源名
            degradeRule.setResource(resource.getId());
            // 平均响应时间阈值 慢调用比例模式下为慢调用临界 RT（超出该值计为慢调用）；异常比例/异常数模式下为对应的阈值
            degradeRule.setCount(
                degradeRuleParamMap.get("count") == null
                    ? 500
                    : Integer.parseInt(degradeRuleParamMap.get("count")));
            // 熔断策略 降级模式，根据评价响应时间降级
            // DEGRADE_GRADE_RT = 0;  根据响应时间
            // DEGRADE_GRADE_EXCEPTION_RATIO = 1;  根据异常比例
            // DEGRADE_GRADE_EXCEPTION_COUNT = 2;   根据异常数量
            degradeRule.setGrade(
                degradeRuleParamMap.get("grade") == null
                    ? RuleConstant.DEGRADE_GRADE_RT
                    : Integer.parseInt(degradeRuleParamMap.get("grade")));
            // 设置  降级时间 熔断时长，单位为 s
            degradeRule.setTimeWindow(
                degradeRuleParamMap.get("timeWindow") == null
                    ? 10
                    : Integer.parseInt(degradeRuleParamMap.get("timeWindow")));
            // 熔断触发的最小请求数，请求数小于该值时即使异常比率超出阈值也不会熔断（1.7.0 引入）
            degradeRule.setMinRequestAmount(
                degradeRuleParamMap.get("minRequestAmount") == null
                    ? 5
                    : Integer.parseInt(degradeRuleParamMap.get("minRequestAmount")));
            // 统计时长（单位为 ms），如 60*1000 代表分钟级（1.8.0 引入）
            degradeRule.setStatIntervalMs(
                degradeRuleParamMap.get("statIntervalMs") == null
                    ? 1000
                    : Integer.parseInt(degradeRuleParamMap.get("statIntervalMs")));
            // 慢调用比例阈值，仅慢调用比例模式有效（1.8.0 引入）
            degradeRule.setSlowRatioThreshold(
                degradeRuleParamMap.get("slowRatioThreshold") == null
                    ? 0.5
                    : Double.parseDouble(degradeRuleParamMap.get("slowRatioThreshold")));

            degradeRules.add(degradeRule);

            break;

          case AUTHORITY_RULE:
            Map<String, String> authorityRulesRuleParamMap =
                resourceConfigParamList.stream()
                    .collect(
                        Collectors.toMap(e -> e.getConfigParam().getCode(), e -> e.getValue()));

            if (authorityRulesRuleParamMap.get("limitApp") != null) {
              AuthorityRule authorityRule = new AuthorityRule();
              authorityRule.setResource(resource.getId());
              authorityRule.setStrategy(
                  authorityRulesRuleParamMap.get("strategy") == null
                      ? RuleConstant.AUTHORITY_WHITE
                      : Integer.parseInt(authorityRulesRuleParamMap.get("strategy")));
              authorityRule.setLimitApp(authorityRulesRuleParamMap.get("limitApp"));

              authorityRules.add(authorityRule);
            }
            break;
        }
      }

      if (CollectionUtils.isEmpty(routeDefinition.getFilters())) {
        routeDefinition.setFilters(null);
      }

      add(routeDefinition);
    }

    GatewayApiDefinitionManager.loadApiDefinitions(apiDefinitions);
    GatewayRuleManager.loadRules(gatewayFlowRules);
    DegradeRuleManager.loadRules(degradeRules);
    AuthorityRuleManager.loadRules(authorityRules);

    List<ParamFlowRule> rules = new ArrayList<>();
    ParamFlowRule rule = new ParamFlowRule();
    // 阈值类型：只支持QPS
    rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
    // 阈值
    rule.setCount(1);
    // 资源名
    rule.setResource("1348554251311255554");
    rule.setParamIdx(0); // 指配热点参数的下标
    // 统计窗口时间长度
    rule.setDurationInSec(10000);
    rule.setClusterMode(true);

    ParamFlowClusterConfig clusterConfig = new ParamFlowClusterConfig();

    clusterConfig.setFlowId(1L);

    rule.setClusterConfig(clusterConfig);
    List<ParamFlowItem> items = new ArrayList<>();
    ParamFlowItem item = new ParamFlowItem();
    item.setClassType(String.class.getTypeName());
    item.setCount(1);
    items.add(item);
    rule.setParamFlowItemList(items);
    rules.add(rule);
    ParamFlowRuleManager.loadRules(rules);

    ClusterParamFlowRuleManager.registerPropertyIfAbsent("autumn-gateway");
    ClusterParamFlowRuleManager.loadRules("autumn-gateway", rules);

    ServerTransportConfig serverTransportConfig = new ServerTransportConfig(18888, 600);
    // 加载配置
    ClusterServerConfigManager.loadGlobalTransportConfig(serverTransportConfig);

    Set<String> nameSpaceSet = new HashSet<String>();
    nameSpaceSet.add("autumn-gateway");
    // 服务配置namespace
    ClusterServerConfigManager.loadServerNamespaceSet(nameSpaceSet);

    // 初始化客户端规则
    ClusterClientConfig clusterClientConfig = new ClusterClientConfig();
    // 指定获取Token超时时间
    clusterClientConfig.setRequestTimeout(1000);
    // Client指定配置
    ClusterClientConfigManager.applyNewConfig(clusterClientConfig);
    // 指定TokenServer的Ip和地址
    ClusterClientAssignConfig clusterClientAssignConfig =
        new ClusterClientAssignConfig("127.0.0.1", 18888);
    // 应用
    ClusterClientConfigManager.applyNewAssignConfig(clusterClientAssignConfig);

    ClusterStateManager.setToServer();
  }

  public RouteDefinition routeDefinitionInit(com.autumn.gateway.entity.Resource resource) {
    RouteDefinition routeDefinition = new RouteDefinition();
    routeDefinition.setId(resource.getId());
    URI uri = UriComponentsBuilder.fromUriString(resource.getUri()).build().toUri();
    routeDefinition.setUri(uri);
    routeDefinition.setPredicates(new ArrayList<>());
    routeDefinition.setFilters(new ArrayList<>());

    if (resource.getGroupId() != null) {
      ResourceGroup group = groupMapper.selectById(resource.getGroupId());
      // 域名过滤
      PredicateDefinition headerPredicate = new PredicateDefinition();
      headerPredicate.setName(
          NameUtils.normalizeRoutePredicateName(HeaderRoutePredicateFactory.class));
      headerPredicate.setArgs(new HashMap<>());
      headerPredicate.getArgs().put(HeaderRoutePredicateFactory.HEADER_KEY, "Host");
      headerPredicate.getArgs().put(HeaderRoutePredicateFactory.REGEXP_KEY, group.getDomain());
      routeDefinition.getPredicates().add(headerPredicate);
    }

    // 路径过滤
    PredicateDefinition pathPredicateDefinition = new PredicateDefinition();
    pathPredicateDefinition.setName(
        NameUtils.normalizeRoutePredicateName(PathRoutePredicateFactory.class));
    pathPredicateDefinition.setArgs(new HashMap<>());

    if (resource.getUrl().contains(",")) {
      String[] urls = resource.getUrl().split(",");
      for (int i = 0; i < urls.length; i++) {
        pathPredicateDefinition.addArg(NameUtils.generateName(i), urls[i]);
      }

    } else {
      pathPredicateDefinition
          .getArgs()
          .put(PathRoutePredicateFactory.PATTERN_KEY, resource.getUrl());
    }

    routeDefinition.getPredicates().add(pathPredicateDefinition);

    List<ResourceConfigInfo> resourceConfigInfoList =
        resourceConfigMapper.selectResourceConfigInfo(
            new QueryWrapper<ResourceConfig>()
                .lambda()
                .eq(ResourceConfig::getResourceId, resource.getId())
                .eq(ResourceConfig::getDeleted, BaseWhether.NO)
                .eq(ResourceConfig::getStatus, BaseStatus.VALID));

    for (ResourceConfigInfo resourceConfigInfo : resourceConfigInfoList) {
      Config config = resourceConfigInfo.getConfig();

      List<ResourceConfigParamPO> resourceConfigParamList =
          resourceConfigParamMapper.selectResourceConfigParamInfo(
              new QueryWrapper<ResourceConfigParam>()
                  .lambda()
                  .eq(ResourceConfigParam::getResourceConfigId, resourceConfigInfo.getId())
                  .eq(ResourceConfigParam::getDeleted, BaseWhether.NO)
                  .eq(ResourceConfigParam::getStatus, BaseStatus.VALID));

      switch (resourceConfigInfo.getConfig().getType()) {
        case FILTER:
          FilterDefinition filterDefinition = new FilterDefinition();
          filterDefinition.setName(config.getCode());
          filterDefinition.setArgs(new HashMap<>());
          for (ResourceConfigParamPO resourceConfigParamPO : resourceConfigParamList) {
            filterDefinition
                .getArgs()
                .put(
                    resourceConfigParamPO.getConfigParam().getCode(),
                    resourceConfigParamPO.getValue());
          }
          routeDefinition.getFilters().add(filterDefinition);
          break;
        case PREDICATE:
          PredicateDefinition predicateDefinition = new PredicateDefinition();
          predicateDefinition.setName(config.getName());
          predicateDefinition.setArgs(new HashMap<>());
          for (ResourceConfigParamPO resourceConfigParamPO : resourceConfigParamList) {
            predicateDefinition
                .getArgs()
                .put(
                    resourceConfigParamPO.getConfigParam().getCode(),
                    resourceConfigParamPO.getValue());
          }
          routeDefinition.getPredicates().add(pathPredicateDefinition);
          break;
      }
    }

    if (CollectionUtils.isEmpty(routeDefinition.getFilters())) {
      routeDefinition.setFilters(null);
    }

    return routeDefinition;
  }

  public void test1() {
    RouteDefinition routeDefinition = new RouteDefinition();
    PredicateDefinition predicateDefinition = new PredicateDefinition();
    Map<String, String> predicateParams = new HashMap<>(8);
    Map<String, String> filterParams = new HashMap<>(8);
    FilterDefinition filterDefinition = new FilterDefinition();
    URI uri = UriComponentsBuilder.fromUriString("lb://test").build().toUri();

    routeDefinition.setId("rateLimitTest");
    // 名称是固定的，spring gateway会根据名称找对应的PredicateFactory
    predicateDefinition.setName("Path");
    /** 通过这个方法获得factory 输出是Host */
    System.out.println(NameUtils.normalizeRoutePredicateName(HostRoutePredicateFactory.class));

    predicateParams.put("pattern", "/rate/**");
    predicateDefinition.setArgs(predicateParams);

    // 名称是固定的，spring gateway会根据名称找对应的FilterFactory
    filterDefinition.setName("RequestRateLimiter");
    // 每秒最大访问次数
    filterParams.put("redis-rate-limiter.replenishRate", "2");
    // 令牌桶最大容量
    filterParams.put("redis-rate-limiter.burstCapacity", "3");
    // 限流策略(#{@BeanName})
    filterParams.put("key-resolver", "#{@hostAddressKeyResolver}");
    // 自定义限流器(#{@BeanName})
    filterParams.put("rate-limiter", "#{@redisRateLimiter}");
    filterDefinition.setArgs(filterParams);

    routeDefinition.setPredicates(Arrays.asList(predicateDefinition));
    routeDefinition.setFilters(Arrays.asList(filterDefinition));

    routeDefinition.setUri(uri);
  }

  public void stripPrefix() {
    FilterDefinition filterDefinition = new FilterDefinition();
    filterDefinition.setName(
        NameUtils.normalizeFilterFactoryName(StripPrefixGatewayFilterFactory.class));
    filterDefinition.setArgs(new HashMap<>());
    filterDefinition.getArgs().put(StripPrefixGatewayFilterFactory.PARTS_KEY, "0");
  }

  public void hystrix() {
    FilterDefinition filterDefinition = new FilterDefinition();
    filterDefinition.setName(
        NameUtils.normalizeFilterFactoryName(HystrixGatewayFilterFactory.class));
    filterDefinition.setArgs(new HashMap<>());
    filterDefinition.getArgs().put("name", "fallbackcmd");
    filterDefinition.getArgs().put("fallbackUri", "forward:/fallback");
  }

  public void retry() {
    FilterDefinition retryFilterDefinition = new FilterDefinition();
    retryFilterDefinition.setName(
        NameUtils.normalizeFilterFactoryName(RetryGatewayFilterFactory.class));
    retryFilterDefinition.setArgs(new HashMap<>());
    retryFilterDefinition.getArgs().put("retries", "3");
    retryFilterDefinition.getArgs().put("series", "SUCCESSFUL,CLIENT_ERROR");
    retryFilterDefinition.getArgs().put("methods", "get");
  }

  public void test() {
    RouteDefinition definition = new RouteDefinition();

    PredicateDefinition predicateDefinition = new PredicateDefinition();
    Map<String, String> predicateParams = new HashMap<>(8);
    definition.setId("cruise-manager");
    predicateDefinition.setName("Path");
    // 请替换成本地可访问的路径
    predicateParams.put("_genkey_0", "/open/api/cruise/manager/**");
    predicateParams.put("_genkey_1", "/api/cruise/manager/**");

    // 请替换成本地可访问的路径
    // predicateParams.put("pathPattern", "/baidu");
    predicateDefinition.setArgs(predicateParams);
    definition.setPredicates(Arrays.asList(predicateDefinition));

    Map<String, String> filterParams = new HashMap<>(8);
    FilterDefinition filterDefinition = new FilterDefinition();

    filterDefinition.setName(
        NameUtils.normalizeFilterFactoryName(AuthenticationGatewayFilterFactory.class));

    filterParams.put("url", "http://localhost:8789/test/get?token=");
    filterParams.put("field", "token");
    filterParams.put("type", "header");
    filterParams.put("roles", "admin,admin1,admin2");
    filterDefinition.setArgs(filterParams);

    FilterDefinition retryFilterDefinition = new FilterDefinition();
    retryFilterDefinition.setName(
        NameUtils.normalizeFilterFactoryName(RetryGatewayFilterFactory.class));
    retryFilterDefinition.setArgs(new HashMap<>());
    retryFilterDefinition.getArgs().put("retries", "3");
    retryFilterDefinition.getArgs().put("series", "SUCCESSFUL,CLIENT_ERROR");
    retryFilterDefinition.getArgs().put("methods", "get");

    definition.setFilters(Arrays.asList(retryFilterDefinition, filterDefinition));

    // 请替换成本地可访问的域名
    // URI uri = UriComponentsBuilder.fromHttpUrl("lb://cruiseroute").build().toUri();
    URI uri = UriComponentsBuilder.fromUriString("lb://cruise-manager").build().toUri();
    definition.setUri(uri);
    // routeDefinitionWriter.save(Mono.just(definition)).subscribe();

    add(definition);
  }

  @Override
  public void updateResource(com.autumn.gateway.entity.Resource resource) {
    RouteDefinition definition = routeDefinitionInit(resource);
    update(definition);
  }

  @Override
  public void addResource(com.autumn.gateway.entity.Resource resource) {
    RouteDefinition definition = routeDefinitionInit(resource);
    add(definition);
  }
}
