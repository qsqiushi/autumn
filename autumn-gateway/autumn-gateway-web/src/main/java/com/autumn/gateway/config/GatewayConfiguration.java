package com.autumn.gateway.config;

/**
 * @program: airlook-dev
 * @description:
 * @author: qius
 * @create: 2021-01-15:19:20
 */
import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** @author Eric Zhao */
@Configuration
public class GatewayConfiguration {

  private final List<ViewResolver> viewResolvers;
  private final ServerCodecConfigurer serverCodecConfigurer;

  public GatewayConfiguration(
      ObjectProvider<List<ViewResolver>> viewResolversProvider,
      ServerCodecConfigurer serverCodecConfigurer) {
    this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
    this.serverCodecConfigurer = serverCodecConfigurer;
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
    // Register the block exception handler for Spring Cloud Gateway.
    return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
  }

  @Bean
  @Order(-1)
  public GlobalFilter sentinelGatewayFilter() {
    return new SentinelGatewayFilter();
  }

  // @PostConstruct
  public void doInit() {
    initCustomizedApis();
    initGatewayRules();
  }

  private void initCustomizedApis() {
    Set<ApiDefinition> definitions = new HashSet<>();
    ApiDefinition api1 =
        new ApiDefinition("some_customized_api")
            .setPredicateItems(
                new HashSet<ApiPredicateItem>() {
                  {
                    add(new ApiPathPredicateItem().setPattern("/ahas"));
                    add(
                        new ApiPathPredicateItem()
                            .setPattern("/product/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                  }
                });
    ApiDefinition api2 =
        new ApiDefinition("another_customized_api")
            .setPredicateItems(
                new HashSet<ApiPredicateItem>() {
                  {
                    add(
                        new ApiPathPredicateItem()
                            .setPattern("/**")
                            .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                  }
                });
    definitions.add(api1);
    definitions.add(api2);
    GatewayApiDefinitionManager.loadApiDefinitions(definitions);
  }

  private void initGatewayRules() {
    Set<GatewayFlowRule> rules = new HashSet<>();
    rules.add(new GatewayFlowRule("aliyun_route").setCount(10).setIntervalSec(1));
    rules.add(
        new GatewayFlowRule("aliyun_route")
            // 限流的阈值  2线程或者2qps
            .setCount(2)
            // 限流阈值类型，qps 或线程数
            .setGrade(RuleConstant.FLOW_GRADE_QPS)
            // intervalSec：统计时间窗口，单位是秒，默认是 1 秒 目前仅对参数限流生效。
            .setIntervalSec(1)
            // 应对突发请求时额外允许的请求数目
            .setBurst(2)
            // 流量整形的控制效果，同限流规则的 controlBehavior 字段，目前支持快速失败和匀速排队两种模式，默认是快速失败。
            .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER)
            // 匀速排队模式下的最长排队时间，单位是毫秒，仅在匀速排队模式下生效。
            .setMaxQueueingTimeoutMs(600)
            // paramItem 参数限流配置。若不提供，则代表不针对参数进行限流，该网关规则将会被转换成普通流控规则；否则会转换成热点规则。其中的字段：
            .setParamItem(
                new GatewayParamFlowItem()
                    // 从请求中提取参数的策略，目前支持提取来源 IP
                    // （PARAM_PARSE_STRATEGY_CLIENT_IP）、Host（PARAM_PARSE_STRATEGY_HOST）、任意
                    // Header（PARAM_PARSE_STRATEGY_HEADER）和任意 URL
                    // 参数（PARAM_PARSE_STRATEGY_URL_PARAM）四种模式。
                    // fieldName：若提取策略选择 Header 模式或 URL 参数模式，则需要指定对应的 header 名称或 URL 参数名称。
                    // pattern 和 matchStrategy：为后续参数匹配特性预留，目前未实现。
                    .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_CLIENT_IP)));
    rules.add(
        new GatewayFlowRule("httpbin_route")
            .setCount(10)
            .setIntervalSec(1)
            .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER)
            .setMaxQueueingTimeoutMs(600)
            .setParamItem(
                new GatewayParamFlowItem()
                    .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_HEADER)
                    .setFieldName("X-Sentinel-Flag")));
    rules.add(
        new GatewayFlowRule("httpbin_route")
            .setCount(1)
            .setIntervalSec(1)
            .setParamItem(
                new GatewayParamFlowItem()
                    .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
                    .setFieldName("pa")));
    rules.add(
        new GatewayFlowRule("httpbin_route")
            .setCount(2)
            .setIntervalSec(30)
            .setParamItem(
                new GatewayParamFlowItem()
                    .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
                    .setFieldName("type")
                    .setPattern("warn")
                    .setMatchStrategy(SentinelGatewayConstants.PARAM_MATCH_STRATEGY_CONTAINS)));

    rules.add(
        new GatewayFlowRule("some_customized_api")
            .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
            .setCount(5)
            .setIntervalSec(1)
            .setParamItem(
                new GatewayParamFlowItem()
                    .setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM)
                    .setFieldName("pn")));
    GatewayRuleManager.loadRules(rules);
  }
}
