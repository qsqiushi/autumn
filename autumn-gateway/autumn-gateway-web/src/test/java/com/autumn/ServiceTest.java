package com.autumn;

import com.autumn.gateway.dto.ResourceDTO;
import com.autumn.gateway.entity.Config;
import com.autumn.gateway.entity.ConfigParam;
import com.autumn.gateway.entity.ResourceConfig;
import com.autumn.gateway.enums.BaseWhetherEnum;
import com.autumn.gateway.mapper.ConfigMapper;
import com.autumn.gateway.mapper.ConfigParamMapper;
import com.autumn.gateway.mapper.ResourceConfigMapper;
import com.autumn.gateway.mapper.ResourceConfigParamMapper;
import com.autumn.gateway.po.ResourceConfigInfo;
import com.autumn.gateway.service.IResourceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: airlook-dev
 * @description:
 * @author: qius
 * @create: 2021-01-07:11:46
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback(false)
public class ServiceTest {

  @Resource private ConfigMapper configMapper;

  @Resource private ConfigParamMapper configParamMapper;

  @Resource private ResourceConfigMapper resourceConfigMapper;

  @Resource private ResourceConfigParamMapper resourceConfigParamMapper;

  @Resource private IResourceService resourceService;

  @Resource private RedisTemplate redisTemplate;

  private static Gson gson = new Gson();

  public static final String GATEWAY_ROUTES = "airlook:gateway:route";

  @Test
  public void saveConfigParam() {

    Config config = configMapper.selectById("1351087501598543874");

    ConfigParam configParam1 = new ConfigParam();
    configParam1.setCode("limitApp");
    configParam1.setConfigId(config.getId());
    configParam1.setName("调用方,逗号隔开");

    ConfigParam configParam2 = new ConfigParam();
    configParam2.setCode("strategy");
    configParam2.setConfigId(config.getId());
    configParam2.setName("授权类型");

    configParamMapper.insert(configParam1);
    configParamMapper.insert(configParam2);
  }

  @Test
  public void testRedis() {

    delete(Mono.just("1348554251311255554")).subscribe();
  }

  public Mono<Void> delete(Mono<String> routeId) {
    log.info("this is  RedisRouteDefinitionRepository del ");

    return routeId.flatMap(
        string -> {
          log.info("del route id[{}]", string);
          if (redisTemplate.opsForHash().hasKey(GATEWAY_ROUTES, string)) {
            redisTemplate.opsForHash().delete(GATEWAY_ROUTES, string);
            log.info(" route id [{}] has bean deleted", string);
            return Mono.empty();
          }
          return Mono.defer(
              () -> Mono.error(new NotFoundException("RouteDefinition not found: " + routeId)));
        });
  }

  @Test
  public void testQuery() {
    List<ResourceConfigInfo> resourceConfigInfoList =
        resourceConfigMapper.selectResourceConfigInfo(
            new QueryWrapper<ResourceConfig>()
                .lambda()
                .eq(ResourceConfig::getResourceId, "1348548369374617601")
                .eq(ResourceConfig::getDeleted, BaseWhetherEnum.NO)
                .eq(ResourceConfig::getStatus, BaseWhetherEnum.YES));

    System.out.println(resourceConfigInfoList.size());
  }

  @Test
  public void resourceConfigSave() {

    ResourceDTO dto = new ResourceDTO();

    dto.setResourceId("1367001845263773697");
    com.autumn.gateway.entity.Resource resource = resourceService.getById(dto.getResourceId());

    Map<String, String> stripPrefixParams = new HashMap<>();
    stripPrefixParams.put("configCode", "StripPrefix");
    stripPrefixParams.put("configType", "FILTER");
    stripPrefixParams.put("parts", "0");

    Map<String, String> hystrixParams = new HashMap<>();

    hystrixParams.put("configCode", "Hystrix");
    hystrixParams.put("configType", "FILTER");
    hystrixParams.put("name", "fallbackcmd");
    hystrixParams.put("fallbackUri", "forward:/fallback");

    Map<String, String> retryParams = new HashMap<>();
    retryParams.put("configCode", "Retry");
    retryParams.put("configType", "FILTER");
    retryParams.put("retries", "3");
    retryParams.put("series", "SERVER_ERROR,CLIENT_ERROR");
    retryParams.put("methods", "get");

    Map<String, String> authenticationFilterParams = new HashMap<>(8);
    authenticationFilterParams.put("configCode", "Authentication");
    authenticationFilterParams.put("configType", "FILTER");
    authenticationFilterParams.put("url", "http://172.16.0.54:8280/api/v1/user/get-by-token/");
    authenticationFilterParams.put("field", "token");
    authenticationFilterParams.put("type", "header");
    authenticationFilterParams.put("roles", "admin,admin1,admin2");

    Map<String, String> gatewayRuleParams = new HashMap<>();
    gatewayRuleParams.put("configCode", "GATEWAY_FLOW_RULE");
    gatewayRuleParams.put("configType", "GATEWAY_FLOW_RULE");
    gatewayRuleParams.put("grade", "1");
    gatewayRuleParams.put("count", "200");
    gatewayRuleParams.put("intervalSec", "1");
    gatewayRuleParams.put("controlBehavior", "2");
    gatewayRuleParams.put("burst", "1");
    gatewayRuleParams.put("maxQueueingTimeoutMs", "600");
    gatewayRuleParams.put("paramItem", "0");
    // retryParams.put("fieldName", "SERVER_ERROR,CLIENT_ERROR");

    Map<String, String> degradeRuleParamMap = new HashMap<>();
    degradeRuleParamMap.put("configCode", "DEGRADE_RULE");
    degradeRuleParamMap.put("configType", "DEGRADE_RULE");
    degradeRuleParamMap.put("grade", "0");
    degradeRuleParamMap.put("count", "5000");
    degradeRuleParamMap.put("timeWindow", "100");
    degradeRuleParamMap.put("minRequestAmount", "5");
    degradeRuleParamMap.put("statIntervalMs", "60000");
    degradeRuleParamMap.put("slowRatioThreshold", "0.5");

    Map<String, String> authorityRulesRuleParamMap = new HashMap<>();

    authorityRulesRuleParamMap.put("configCode", "AUTHORITY_RULE");
    authorityRulesRuleParamMap.put("configType", "AUTHORITY_RULE");
    authorityRulesRuleParamMap.put("limitApp", "www.baidu.com");
    authorityRulesRuleParamMap.put("strategy", "0");

    Map<String, String> cacheResponseFilterParamMap = new HashMap<>();

    cacheResponseFilterParamMap.put("configCode", "CacheResponse");
    cacheResponseFilterParamMap.put("configType", "FILTER");
    cacheResponseFilterParamMap.put("media", "memory");


    Map<String, String> respondCacheFilterParamMap = new HashMap<>();

    respondCacheFilterParamMap.put("configCode", "RespondCache");
    respondCacheFilterParamMap.put("configType", "FILTER");


    dto.setParams(new ArrayList<>());
    dto.getParams().add(stripPrefixParams);
    dto.getParams().add(hystrixParams);
    dto.getParams().add(retryParams);
    // dto.getParams().add(authenticationFilterParams);
    dto.getParams().add(gatewayRuleParams);
    dto.getParams().add(degradeRuleParamMap);
    // dto.getParams().add(authorityRulesRuleParamMap);

    dto.getParams().add(cacheResponseFilterParamMap);

    dto.getParams().add(respondCacheFilterParamMap);

    resourceService.saveConfig(resource, dto.getParams());
  }

  @Test
  public void testResourceConfigParamMapper() {
    System.out.println(
        new Gson()
            .toJson(resourceConfigParamMapper.selectResourceConfigParamInfo(new QueryWrapper<>())));
  }

  @Test
  public void configMapperTest() {

    System.out.println(configMapper == null);

    System.out.println(configMapper.selectCount(new QueryWrapper<>()));

    System.out.println(
        new Gson().toJson(resourceConfigMapper.selectResourceConfigInfo(new QueryWrapper<>())));
  }

  @Test
  public void loginSave() {

    ResourceDTO dto = new ResourceDTO();

    // dto.setResourceId("1348554251311255565");
    dto.setResourceId("1348554251311255566");
    com.autumn.gateway.entity.Resource resource = resourceService.getById(dto.getResourceId());

    Map<String, String> stripPrefixParams = new HashMap<>();
    stripPrefixParams.put("configCode", "StripPrefix");
    stripPrefixParams.put("configType", "FILTER");
    stripPrefixParams.put("parts", "0");

    Map<String, String> hystrixParams = new HashMap<>();

    hystrixParams.put("configCode", "Hystrix");
    hystrixParams.put("configType", "FILTER");
    hystrixParams.put("name", "fallbackcmd");
    hystrixParams.put("fallbackUri", "forward:/fallback");

    Map<String, String> retryParams = new HashMap<>();
    retryParams.put("configCode", "Retry");
    retryParams.put("configType", "FILTER");
    retryParams.put("retries", "3");
    retryParams.put("series", "SERVER_ERROR,CLIENT_ERROR");
    retryParams.put("methods", "get");

    Map<String, String> authenticationFilterParams = new HashMap<>(8);
    authenticationFilterParams.put("configCode", "Authentication");
    authenticationFilterParams.put("configType", "FILTER");
    authenticationFilterParams.put("url", "http://172.16.0.54:8280/api/v1/user/get-by-token/");
    authenticationFilterParams.put("field", "token");
    authenticationFilterParams.put("type", "header");
    authenticationFilterParams.put("roles", "admin,admin1,admin2");

    dto.setParams(new ArrayList<>());
    dto.getParams().add(stripPrefixParams);
    dto.getParams().add(hystrixParams);
    dto.getParams().add(retryParams);
    // dto.getParams().add(authenticationFilterParams);

    resourceService.saveConfig(resource, dto.getParams());
  }

  @Test
  public void openApiSave() {

    ResourceDTO dto = new ResourceDTO();

    dto.setResourceId("1348554251311255563");
    com.autumn.gateway.entity.Resource resource = resourceService.getById(dto.getResourceId());

    Map<String, String> stripPrefixParams = new HashMap<>();
    stripPrefixParams.put("configCode", "StripPrefix");
    stripPrefixParams.put("configType", "FILTER");
    stripPrefixParams.put("parts", "0");

    Map<String, String> hystrixParams = new HashMap<>();

    hystrixParams.put("configCode", "Hystrix");
    hystrixParams.put("configType", "FILTER");
    hystrixParams.put("name", "fallbackcmd");
    hystrixParams.put("fallbackUri", "forward:/fallback");

    Map<String, String> retryParams = new HashMap<>();
    retryParams.put("configCode", "Retry");
    retryParams.put("configType", "FILTER");
    retryParams.put("retries", "3");
    retryParams.put("series", "SERVER_ERROR,CLIENT_ERROR");
    retryParams.put("methods", "get");

    Map<String, String> openApiParams = new HashMap<>(8);
    openApiParams.put("configCode", "OpenApi");
    openApiParams.put("configType", "FILTER");
    openApiParams.put("url", "http://172.16.0.54:8280/api/v1/user/get-by-ak/");
    openApiParams.put("roles", "admin,admin1,admin2");

    dto.setParams(new ArrayList<>());
    dto.getParams().add(stripPrefixParams);
    dto.getParams().add(hystrixParams);
    dto.getParams().add(retryParams);
    dto.getParams().add(openApiParams);

    resourceService.saveConfig(resource, dto.getParams());
  }
}
