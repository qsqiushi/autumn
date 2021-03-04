package com.autumn.gateway.common;

/**
 * @program: dataearth-online-cloud
 * @description: 常量类
 * @author: qiushi
 * @create: 2019-01-31 16:55
 */
public class GateWayConstants {

  /** open api 鉴权头 */
  public static final String OPEN_API_HEADER_AUTHORIZATION = "Authorization";

  /** open api requestBody 缓存名称 */
  public static final String OPEN_API_ATTRIBUTE_CACHED_REQUEST_BODY_OBJECT =
      "cachedRequestBodyObject";

  /** open api 请求 有效时长 毫秒 */
  public static final Long OPEN_API_REQUEST_ACTIVE_TIME = new Long(1000 * 60 * 5);

  /** 内存缓存beanName */
  public static final String MEMORY_CACHE_BEAN_NAME = "strCache";

  /** 缓存响应结果的过滤器顺序 */
  public static final Integer GATEWAY_FILTER_ORDER_CACHE_RESPONSE = -2;

  /** 响应缓存作为结果的过滤器顺序 */
  public static final Integer GATEWAY_FILTER_ORDER_RESPOND_CACHE = -3;

  /** open api 鉴权过滤器顺序 */
  public static final Integer GATEWAY_FILTER_ORDER_OPEN_API_AUTH = -4;
  /** 鉴权过滤器顺序 */
  public static final Integer GATEWAY_FILTER_ORDER_API_AUTH = -4;

  /** 熔断过滤器顺序 */
  public static final Integer GATEWAY_FILTER_ORDER_HYSTRIX = -5;
}
