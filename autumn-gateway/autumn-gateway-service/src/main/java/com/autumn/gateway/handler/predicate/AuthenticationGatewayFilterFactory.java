package com.autumn.gateway.handler.predicate;

import com.autumn.bo.HttpParams;
import com.autumn.gateway.bo.UserInfo;
import com.autumn.gateway.common.GateWayConstants;
import com.autumn.gateway.utils.PredicateHandlerUtils;
import com.autumn.model.enums.ResultCode;
import com.autumn.model.web.BaseResponse;
import com.autumn.model.web.ResponseBuilder;
import com.autumn.utils.HttpClientUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @program: autumn-passport
 * @description: 鉴权路由工厂
 * @author: qiushi
 * @create: 2020-12-22:09:36
 */
@Slf4j
public class AuthenticationGatewayFilterFactory
    extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {
  private static final String FIELD = "field";
  private static final String URL = "url";
  private static final String TYPE = "type";
  private static final String ROLES = "roles";
  private static final String TYPE_HEADER = "header";
  private static final String TYPE_COOKIE = "cookie";

  static {
    log.info("this is AuthenticationGatewayFilterFactory ");
  }

  private HttpClientBuilder httpClientBuilder;

  public AuthenticationGatewayFilterFactory() {
    super(Config.class);
  }

  public AuthenticationGatewayFilterFactory setHttpClientBuilder(
      HttpClientBuilder httpClientBuilder) {
    this.httpClientBuilder = httpClientBuilder;
    return this;
  }

  @Override
  public GatewayFilter apply(Config config) {

    return new OrderedGatewayFilter(
        new GatewayFilter() {
          @Override
          public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

            if (StringUtils.isEmpty(config.getField()) || StringUtils.isEmpty(config.getUrl())) {
              String msg =
                  ResponseBuilder.failStr(ResultCode.BAD_REQUEST.getCode(), "setting error");
              return PredicateHandlerUtils.error(exchange, msg);
            }
            String token = getToken(exchange, config);
            if (StringUtils.isEmpty(token)) {
              String msg =
                  ResponseBuilder.failStr(
                      ResultCode.BAD_REQUEST.getCode(), config.getField() + " not found");
              return PredicateHandlerUtils.error(exchange, msg);
            }
            HttpParams httpParams = new HttpParams();
            httpParams.setUrl(config.getUrl() + token);
            String result = HttpClientUtils.get(httpClientBuilder.build(), httpParams);
            if (StringUtils.isEmpty(result)) {
              log.error("鉴权失败");
              String msg = ResponseBuilder.failStr(ResultCode.BAD_REQUEST.getCode(), "remote fail");
              return PredicateHandlerUtils.error(exchange, msg);
            }
            Gson gson = new Gson();
            Type type = new TypeToken<BaseResponse<UserInfo>>() {}.getType();
            BaseResponse<UserInfo> baseResponse;
            try {
              baseResponse = gson.fromJson(result, type);
            } catch (Exception ex) {
              log.error("serialize exception", ex);

              String msg =
                  ResponseBuilder.failStr(ResultCode.BAD_REQUEST.getCode(), "gson serialize fail");
              return PredicateHandlerUtils.error(exchange, msg);
            }

            if (baseResponse.getCode() != ResultCode.SUCCESS.getCode()) {
              log.error("鉴权失败");
              String msg =
                  ResponseBuilder.failStr(ResultCode.BAD_REQUEST.getCode(), "access error");
              return PredicateHandlerUtils.error(exchange, msg);
            }

            UserInfo userInfo = baseResponse.getData();

            // 角色判断
            List<String> roles = config.getRoles();

            if (!CollectionUtils.isEmpty(roles)) {
              roles.retainAll(userInfo.getRoleCodes());
              if (CollectionUtils.isEmpty(roles)) {
                log.error("角色不符");
                String msg =
                    ResponseBuilder.failStr(ResultCode.BAD_REQUEST.getCode(), "access error");
                return PredicateHandlerUtils.error(exchange, msg);
              }
            }

            try {
              userInfo.setUserName(
                  URLEncoder.encode(
                      StringUtils.defaultIfEmpty(userInfo.getUserName(), ""),
                      StandardCharsets.UTF_8.displayName()));
              userInfo.setLoginName(
                  URLEncoder.encode(
                      StringUtils.defaultIfEmpty(userInfo.getLoginName(), ""),
                      StandardCharsets.UTF_8.displayName()));

            } catch (Exception ex) {
              log.error("转换编码异常", ex);
              String msg = ResponseBuilder.failStr(ResultCode.BAD_REQUEST.getCode(), "sys error");
              return PredicateHandlerUtils.error(exchange, msg);
            }

            ServerWebExchange serverWebExchange =
                PredicateHandlerUtils.setUserInfo2Header(exchange, userInfo);

            return chain.filter(serverWebExchange);
          }
        },
            GateWayConstants.GATEWAY_FILTER_ORDER_API_AUTH);
  }

  //  ShortcutType，该值是枚举类型，分别是
  //  DEFAULT :按照shortcutFieldOrder顺序依次赋值
  //  GATHER_LIST：shortcutFiledOrder只能有一个值,如果参数有多个拼成一个集合
  //  GATHER_LIST_TAIL_FLAG：shortcutFiledOrder只能有两个值，其中最后一个值为true或者false，其余的值变成一个集合付给第一个值

  //  @Override
  //  public ShortcutType shortcutType() {
  //    return ShortcutType.GATHER_LIST;
  //  }
  //  shortcutFieldOrder,这个值决定了Config中配置的属性，配置的参数都会被封装到该属性当中

  @Override
  public List<String> shortcutFieldOrder() {
    return Arrays.asList(URL, FIELD, TYPE, ROLES);
  }

  public String getToken(ServerWebExchange exchange, Config config) {

    if (StringUtils.equals(config.getType(), TYPE_HEADER)) {

      return getTokenFromHeader(exchange, config.getField());
    } else if (StringUtils.equals(config.getType(), TYPE_COOKIE)
        || StringUtils.isEmpty(config.getType())) {

      return getTokenFromCookie(exchange, config.getField());
    } else {
      return null;
    }
  }

  private String getTokenFromCookie(ServerWebExchange exchange, String field) {
    MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
    if (cookies == null || cookies.size() == 0) {
      return null;
    }
    if (cookies != null && cookies.getFirst(field) != null) {
      return cookies.getFirst(field).getValue();
    } else {
      return null;
    }
  }

  public String getTokenFromHeader(ServerWebExchange exchange, String field) {
    HttpHeaders headers = exchange.getRequest().getHeaders();
    return headers.getFirst(field);
  }

  @Validated
  @Data
  @NoArgsConstructor
  @EqualsAndHashCode(callSuper = false)
  @Accessors(chain = true)
  public static class Config {
    private String field;
    private String url;
    private String type;
    private List<String> roles;
  }
}
