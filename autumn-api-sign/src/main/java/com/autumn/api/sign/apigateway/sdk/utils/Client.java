package com.autumn.api.sign.apigateway.sdk.utils;

import com.autumn.api.sign.sdk.http.HttpMethodName;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

public class Client {
  public static HttpRequestBase sign(Request request) throws Exception {
    String appKey = request.getKey();
    String appSecrect = request.getSecret();

    String url = request.getUrl();
    String body = request.getBody();
    Map<String, String> headers = request.getHeaders();

    switch (request.getMethod()) {
      case GET:
        return get(appKey, appSecrect, url, headers);
      case POST:
        return post(appKey, appSecrect, url, headers, body);
      case PUT:
        return put(appKey, appSecrect, url, headers, body);
      case PATCH:
        return patch(appKey, appSecrect, url, headers, body);
      case DELETE:
        return delete(appKey, appSecrect, url, headers);
      case HEAD:
        return head(appKey, appSecrect, url, headers);
      case OPTIONS:
        return options(appKey, appSecrect, url, headers);
    }
    throw new IllegalArgumentException(
        String.format("unsupported method:%s", new Object[] {request.getMethod().name()}));
  }

  public static HttpRequestBase post(
      String ak, String sk, String requestUrl, Map<String, String> headers, String postbody)
      throws Exception {
    AccessService accessService = new AccessServiceImpl(ak, sk);
    if (postbody == null) {
      postbody = "";
    }
    HttpMethodName httpMethod = HttpMethodName.POST;
    byte[] bytes = postbody.getBytes("UTF-8");
    InputStream content = new ByteArrayInputStream(bytes);
    HttpRequestBase request =
        accessService.access(requestUrl, headers, content, Long.valueOf(bytes.length), httpMethod);
    return request;
  }

  public static HttpRequestBase put(
      String ak, String sk, String requestUrl, Map<String, String> headers, String putBody)
      throws Exception {
    AccessService accessService = new AccessServiceImpl(ak, sk);
    HttpMethodName httpMethod = HttpMethodName.PUT;
    if (putBody == null) {
      putBody = "";
    }
    byte[] bytes = putBody.getBytes("UTF-8");
    InputStream content = new ByteArrayInputStream(bytes);
    HttpRequestBase request =
        accessService.access(requestUrl, headers, content, Long.valueOf(bytes.length), httpMethod);
    return request;
  }

  public static HttpRequestBase patch(
      String ak, String sk, String requestUrl, Map<String, String> headers, String body)
      throws Exception {
    AccessService accessService = new AccessServiceImpl(ak, sk);
    HttpMethodName httpMethod = HttpMethodName.PATCH;
    if (body == null) {
      body = "";
    }
    byte[] bytes = body.getBytes("UTF-8");
    InputStream content = new ByteArrayInputStream(bytes);
    HttpRequestBase request =
        accessService.access(requestUrl, headers, content, Long.valueOf(bytes.length), httpMethod);
    return request;
  }

  public static HttpRequestBase delete(
      String ak, String sk, String requestUrl, Map<String, String> headers) throws Exception {
    AccessService accessService = new AccessServiceImpl(ak, sk);

    HttpMethodName httpMethod = HttpMethodName.DELETE;
    HttpRequestBase request = accessService.access(requestUrl, headers, httpMethod);
    return request;
  }

  public static HttpRequestBase get(
      String ak, String sk, String requestUrl, Map<String, String> headers) throws Exception {
    AccessService accessService = new AccessServiceImpl(ak, sk);

    HttpMethodName httpMethod = HttpMethodName.GET;
    HttpRequestBase request = accessService.access(requestUrl, headers, httpMethod);
    return request;
  }

  public static HttpRequestBase head(
      String ak, String sk, String requestUrl, Map<String, String> headers) throws Exception {
    AccessService accessService = new AccessServiceImpl(ak, sk);

    HttpMethodName httpMethod = HttpMethodName.HEAD;
    HttpRequestBase request = accessService.access(requestUrl, headers, httpMethod);
    return request;
  }

  public static HttpRequestBase options(
      String ak, String sk, String requestUrl, Map<String, String> headers) throws Exception {
    AccessService accessService = new AccessServiceImpl(ak, sk);

    HttpMethodName httpMethod = HttpMethodName.OPTIONS;
    HttpRequestBase request = accessService.access(requestUrl, headers, httpMethod);
    return request;
  }

  public static okhttp3.Request okhttpRequest(
      HttpMethodName httpMethod,
      String ak,
      String sk,
      String requestUrl,
      Map<String, String> headers,
      String body)
      throws Exception {
    switch (httpMethod) {
      case POST:
      case PUT:
      case PATCH:
      case DELETE:
        break;
      case GET:
      case HEAD:
      case OPTIONS:
        body = "";
        break;
      default:
        throw new RuntimeException("Unknown HTTP method name: " + httpMethod);
    }
    if (body == null) {
      body = "";
    }
    AccessServiceOkhttp accessService = new AccessServiceOkhttpImpl(ak, sk);
    okhttp3.Request request = accessService.access(requestUrl, headers, body, httpMethod);
    return request;
  }

  public static okhttp3.Request signOkhttp(Request request) throws Exception {
    return okhttpRequest(
        request.getMethod(),
        request.getKey(),
        request.getSecret(),
        request.getUrl(),
        request.getHeaders(),
        request.getBody());
  }
}
