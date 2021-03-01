package com.autumn.gateway.sign;

import com.autumn.api.sign.apigateway.sdk.utils.CheckSignResult;
import com.autumn.api.sign.sdk.DefaultRequest;
import com.autumn.api.sign.sdk.Request;
import com.autumn.api.sign.sdk.auth.credentials.BasicCredentials;
import com.autumn.api.sign.sdk.auth.signer.Signer;
import com.autumn.api.sign.sdk.auth.signer.SignerFactory;
import com.autumn.api.sign.sdk.auth.signer.internal.SignerUtils;
import com.autumn.api.sign.sdk.http.HttpMethodName;
import com.autumn.api.sign.sdk.util.StringUtils;
import io.netty.buffer.ByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

public class Server {
  private static final Logger log = LoggerFactory.getLogger(Server.class);

  public Server() {}

  public static String sign(String appKey, String appSecret, ServerWebExchange exchange) {
    HttpMethodName httpMethodName =
        (HttpMethodName)
            Enum.valueOf(
                HttpMethodName.class, exchange.getRequest().getMethodValue().toUpperCase());
    switch (httpMethodName) {
      case GET:
        return get(appKey, appSecret, exchange);
      case POST:
        return post(appKey, appSecret, exchange);
      case PUT:
        return put(appKey, appSecret, exchange);
      case PATCH:
        return patch(appKey, appSecret, exchange);
      case DELETE:
        return delete(appKey, appSecret, exchange);
      case HEAD:
        return head(appKey, appSecret, exchange);
      case OPTIONS:
        return options(appKey, appSecret, exchange);
      default:
        throw new IllegalArgumentException(
            String.format("unsupported method:%s", httpMethodName.name()));
    }
  }

  public static CheckSignResult check(
      String appKey, String appSecret, ServerWebExchange exchange, Long time) {
    ServerHttpRequest request = exchange.getRequest();
    String xSdkDate = request.getHeaders().getFirst("X-Sdk-Date");
    if (StringUtils.isNullOrEmpty(xSdkDate)) {
      return CheckSignResult.FAIL_HEADER_X_SDK_DATE;
    } else {
      Long xSdkDateMillis = null;

      try {
        xSdkDateMillis = SignerUtils.parseMillis(xSdkDate);
      } catch (Exception var10) {
        return CheckSignResult.FAIL_HEADER_X_SDK_DATE;
      }

      if (System.currentTimeMillis() - xSdkDateMillis <= time
          && System.currentTimeMillis() - xSdkDateMillis >= 0L) {
        String sign = null;

        try {
          sign = sign(appKey, appSecret, exchange);
        } catch (Exception var9) {
          log.error("签名出现异常:[{}]", var9);
          return CheckSignResult.FAIL_SIGN_EXCEPTION;
        }

        if (sign == null) {
          return CheckSignResult.FAIL_SIGN_EXCEPTION;
        } else {
          return sign.equals(request.getHeaders().getFirst("Authorization"))
              ? CheckSignResult.SUCCESS
              : CheckSignResult.FAIL_MATCH;
        }
      } else {
        return CheckSignResult.FAIL_TIMEOUT;
      }
    }
  }

  private static String head(String appKey, String appSecret, ServerWebExchange exchange) {
    HttpMethodName httpMethod = HttpMethodName.PATCH;
    return getSign(appKey, appSecret, exchange, httpMethod);
  }

  private static String options(String appKey, String appSecret, ServerWebExchange exchange) {
    HttpMethodName httpMethod = HttpMethodName.OPTIONS;
    return getSign(appKey, appSecret, exchange, httpMethod);
  }

  private static String post(String appKey, String appSecret, ServerWebExchange exchange) {
    HttpMethodName httpMethod = HttpMethodName.POST;
    return getSign(appKey, appSecret, exchange, httpMethod);
  }

  private static String patch(String appKey, String appSecret, ServerWebExchange exchange) {
    HttpMethodName httpMethod = HttpMethodName.PATCH;
    return getSign(appKey, appSecret, exchange, httpMethod);
  }

  private static String put(String appKey, String appSecret, ServerWebExchange exchange) {
    HttpMethodName httpMethod = HttpMethodName.PUT;
    return getSign(appKey, appSecret, exchange, httpMethod);
  }

  public static String get(String ak, String sk, ServerWebExchange exchange) {
    HttpMethodName httpMethod = HttpMethodName.GET;
    return getSign(ak, sk, exchange, httpMethod);
  }

  public static String delete(String ak, String sk, ServerWebExchange exchange) {
    HttpMethodName httpMethod = HttpMethodName.DELETE;
    return getSign(ak, sk, exchange, httpMethod);
  }

  private static String getSign(
      String appKey, String appSecret, ServerWebExchange exchange, HttpMethodName httpMethod) {
    try {
      Request request = new DefaultRequest();
      ServerHttpRequest serverHttpRequest = exchange.getRequest();
      String queryString = serverHttpRequest.getURI().getQuery();
      if (!StringUtils.isNullOrEmpty(queryString)) {
        String[] parameterArray = queryString.split("&");
        int parameterArrayLength = parameterArray.length;

        for (int temp = 0; temp < parameterArrayLength; ++temp) {
          String p = parameterArray[temp];
          String[] p_split = p.split("=", 2);
          String key = p_split[0];
          String value = "";
          if (p_split.length >= 2) {
            value = p_split[1];
          }

          request.addParameter(URLDecoder.decode(key, "UTF-8"), URLDecoder.decode(value, "UTF-8"));
        }
      }

      String wholeStr = (String) exchange.getAttribute("cachedRequestBodyObject");
      if (!StringUtils.isNullOrEmpty(wholeStr)) {
        byte[] bytes = wholeStr.getBytes("UTF-8");
        InputStream content = new ByteArrayInputStream(bytes);
        request.setContent(content);
      }

      request.setHttpMethod(httpMethod);
      request.setEndpoint(serverHttpRequest.getURI());
      HttpHeaders httpHeaders = serverHttpRequest.getHeaders();
      if (httpHeaders.getFirst("Content-Type") != null) {
        request.addHeader("Content-Type", httpHeaders.getFirst("Content-Type"));
      } else {
        request.addHeader("Content-Type", "application/json;charset=UTF-8");
      }

      request.addHeader("X-Sdk-Date", httpHeaders.getFirst("X-Sdk-Date"));
      Signer signer = SignerFactory.getSigner();
      signer.sign(request, new BasicCredentials(appKey, appSecret));
      return request.getHeaders().get("Authorization").toString();
    } catch (Exception var14) {
      var14.printStackTrace();
      return null;
    }
  }

  private static String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
    Flux<DataBuffer> body = serverHttpRequest.getBody();
    StringBuilder sb = new StringBuilder();
    body.subscribe(
        (buffer) -> {
          byte[] bytes = new byte[buffer.readableByteCount()];
          buffer.read(bytes);
          DataBufferUtils.release(buffer);
          String bodyString = new String(bytes, StandardCharsets.UTF_8);
          sb.append(bodyString);
        });
    return sb.toString();
  }

  private String resolveBodyFromRequest2(ServerHttpRequest serverHttpRequest) {
    Flux<DataBuffer> body = serverHttpRequest.getBody();
    AtomicReference<String> bodyRef = new AtomicReference();
    body.subscribe(
        (buffer) -> {
          CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
          DataBufferUtils.release(buffer);
          bodyRef.set(charBuffer.toString());
        });
    return (String) bodyRef.get();
  }

  private DataBuffer stringBuffer(String value) {
    byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
    NettyDataBufferFactory nettyDataBufferFactory =
        new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
    DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
    buffer.write(bytes);
    return buffer;
  }
}
