package com.autumn.gateway.handler.predicate;

import com.autumn.api.sign.apigateway.sdk.utils.CheckSignResult;
import com.autumn.bo.HttpParams;
import com.autumn.gateway.bo.SignInfo;
import com.autumn.gateway.common.GateWayConstants;
import com.autumn.gateway.sign.Server;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @program: airlook-dev
 * @description: 开放接口过滤器
 * @author: qius
 * @create: 2020-12-24:17:53
 */
@Slf4j
public class OpenApiGatewayFilterFactory
    extends AbstractGatewayFilterFactory<OpenApiGatewayFilterFactory.Config> {

  private static final String URL = "url";
  private static final String ROLES = "roles";
  private HttpClientBuilder httpClientBuilder;

  public OpenApiGatewayFilterFactory() {
    super(Config.class);
  }

  public OpenApiGatewayFilterFactory setHttpClientBuilder(HttpClientBuilder httpClientBuilder) {
    this.httpClientBuilder = httpClientBuilder;
    return this;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return new OrderedGatewayFilter(
        new GatewayFilter() {
          /**
           * Process the Web request and (optionally) delegate to the next {@code WebFilter} through
           * the given {@link GatewayFilterChain}.
           *
           * @param exchange the current server exchange
           * @param chain provides a way to delegate to the next filter
           * @return {@code Mono<Void>} to indicate when request processing is complete
           */
          @Override
          public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

            ServerHttpRequest request = exchange.getRequest();

            ServerHttpResponse response = exchange.getResponse();

            HttpHeaders headers = request.getHeaders();

            String authorization = headers.getFirst(GateWayConstants.OPEN_API_HEADER_AUTHORIZATION);
            try {
              String requestBodyStr =
                  exchange.getAttribute(
                      GateWayConstants.OPEN_API_ATTRIBUTE_CACHED_REQUEST_BODY_OBJECT);

              if (StringUtils.isNotEmpty(requestBodyStr)) {

                requestBodyStr = requestBodyStr.replaceAll("\r|\n", "");

                log.info("request body is:{}", requestBodyStr);
              }
            } catch (Exception ex) {
              log.error("获取request body出现异常", ex);
            }

            if (!StringUtils.isEmpty(authorization)) {

              if (!authorization.contains(",")) {
                log.error("签名信息不包含[,]");
                String msg =
                    ResponseBuilder.failStr(
                        ResultCode.BAD_REQUEST.getCode(), "authorization error");
                return PredicateHandlerUtils.error(exchange, msg);
              }

              String[] authorizationSplit = authorization.split(",");

              if (!authorizationSplit[0].contains("=")) {
                log.error("签名信息不包含[=]");
                String msg =
                    ResponseBuilder.failStr(
                        ResultCode.BAD_REQUEST.getCode(), "authorization error");
                return PredicateHandlerUtils.error(exchange, msg);
              }

              String ak = authorizationSplit[0].split("=")[1];

              log.info("ak :[{}]", ak);

              HttpParams httpParams = new HttpParams();
              httpParams.setUrl(config.getUrl() + ak);
              String result = HttpClientUtils.get(httpClientBuilder.build(), httpParams);
              if (StringUtils.isEmpty(result)) {
                log.error("鉴权失败");
                String msg =
                    ResponseBuilder.failStr(ResultCode.BAD_REQUEST.getCode(), "remote fail");
                return PredicateHandlerUtils.error(exchange, msg);
              }
              Gson gson = new Gson();
              Type type = new TypeToken<BaseResponse<SignInfo>>() {}.getType();
              BaseResponse<SignInfo> baseResponse;
              try {
                baseResponse = gson.fromJson(result, type);
              } catch (Exception ex) {
                log.error("serialize exception", ex);

                String msg =
                    ResponseBuilder.failStr(
                        ResultCode.BAD_REQUEST.getCode(), "gson serialize fail");
                return PredicateHandlerUtils.error(exchange, msg);
              }

              if (baseResponse.getCode() != ResultCode.SUCCESS.getCode()) {
                log.error("获取SK失败");
                String msg =
                    ResponseBuilder.failStr(ResultCode.BAD_REQUEST.getCode(), "remote fail");
                return PredicateHandlerUtils.error(exchange, msg);
              }
              SignInfo signInfo = baseResponse.getData();
              String sk = baseResponse.getData().getSk();

              CheckSignResult checkSignResult =
                  Server.check(ak, sk, exchange, GateWayConstants.OPEN_API_REQUEST_ACTIVE_TIME);

              if (checkSignResult == CheckSignResult.SUCCESS) {

                log.info("sign [{}]请求比较结果[{}]", request.getMethodValue(), checkSignResult.name());

                // 角色判断
                List<String> roles = config.getRoles();

                if (!CollectionUtils.isEmpty(roles)) {
                  roles.retainAll(signInfo.getUserInfo().getRoleCodes());
                  if (CollectionUtils.isEmpty(roles)) {
                    log.error("角色不符");
                    String msg =
                        ResponseBuilder.failStr(ResultCode.BAD_REQUEST.getCode(), "access error");
                    return PredicateHandlerUtils.error(exchange, msg);
                  }
                }

                try {
                  signInfo
                      .getUserInfo()
                      .setUserName(
                          URLEncoder.encode(
                              StringUtils.defaultIfEmpty(signInfo.getUserInfo().getUserName(), ""),
                              StandardCharsets.UTF_8.displayName()));
                  signInfo
                      .getUserInfo()
                      .setLoginName(
                          URLEncoder.encode(
                              StringUtils.defaultIfEmpty(signInfo.getUserInfo().getLoginName(), ""),
                              StandardCharsets.UTF_8.displayName()));

                } catch (Exception ex) {
                  log.error("转换编码异常", ex);
                  String msg =
                      ResponseBuilder.failStr(ResultCode.BAD_REQUEST.getCode(), "sys error");
                  return PredicateHandlerUtils.error(exchange, msg);
                }

                ServerWebExchange serverWebExchange =
                    PredicateHandlerUtils.setUserInfo2Header(exchange, signInfo.getUserInfo());

                return chain.filter(serverWebExchange);

              } else {

                String msg = ResponseBuilder.failStr(ResultCode.BAD_REQUEST.getCode(), "sign fail");
                return PredicateHandlerUtils.error(exchange, msg);
              }

            } else {
              log.error("签名信息不存在");
              String msg =
                  ResponseBuilder.failStr(
                      ResultCode.BAD_REQUEST.getCode(), "authorization not found");
              return PredicateHandlerUtils.error(exchange, msg);
            }
          }
        },
        2);
  }

  @Override
  public List<String> shortcutFieldOrder() {
    return Arrays.asList(URL, ROLES);
  }

  @Validated
  @Data
  @NoArgsConstructor
  @EqualsAndHashCode(callSuper = false)
  @Accessors(chain = true)
  public static class Config {

    private String url;
    private List<String> roles;
  }
}
