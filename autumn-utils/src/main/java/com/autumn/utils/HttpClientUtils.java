package com.autumn.utils;

import com.autumn.bo.HttpParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Description: httpclient帮助类
 *
 * @author: qiushi
 * @date: 2018年6月8日 下午1:47:08
 */
@Slf4j
public class HttpClientUtils {

  public static String get(CloseableHttpClient client, HttpParams httpParams) {
    if (httpParams == null) {
      return null;
    }
    // 默认utf-8
    if (StringUtils.isEmpty(httpParams.getCharset())) {
      httpParams.setCharset(StandardCharsets.UTF_8.toString());
    }

    CloseableHttpResponse closeableHttpResponse = null;

    String responseString = null;
    try {

      URIBuilder uriBuilder = new URIBuilder(httpParams.getUrl());
      /** 第一种添加参数的形式 */
      /*uriBuilder.addParameter("name", "root");
      uriBuilder.addParameter("password", "123456");*/
      // 装配Get请求参数
      if (httpParams.getParams() != null) {
        List<NameValuePair> list = new LinkedList<>();
        for (Map.Entry<String, String> entry : httpParams.getParams().entrySet()) {
          // 请求参数
          list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        uriBuilder.setParameters(list);
      }

      HttpGet httpGet = new HttpGet(uriBuilder.build());

      log.info(httpGet.getURI().toString());
      // 单位毫秒
      RequestConfig requestConfig = getRequestConfig(httpParams);

      httpGet.setConfig(requestConfig);

      // 设置header
      if (httpParams.getHeaders() != null) {
        for (Map.Entry<String, String> entry : httpParams.getHeaders().entrySet()) {
          httpGet.addHeader(entry.getKey(), entry.getValue());
        }
      }

      // 返回请求执行结果
      closeableHttpResponse = client.execute(httpGet);
      // 获取返回的状态值
      int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();
      if (statusCode != HttpStatus.SC_OK) {
        return null;
      } else {
        responseString =
            EntityUtils.toString(closeableHttpResponse.getEntity(), httpParams.getCharset());
      }
    } catch (Exception e) {
      log.error("Get请求出现异常", e);
      throw new RuntimeException("Get请求出现异常", e);
    } finally {
      // 关闭
      if (closeableHttpResponse != null) {
        try {
          closeableHttpResponse.close();
        } catch (IOException e) {
          log.error("Get请求关闭closeableHttpResponse出现异常", e);
        }
      }
    }
    return responseString;
  }

  /**
   * @Description: @Param: [client, httpParams]
   *
   * @return: java.lang.String @Author: qiushi @Date: 2019-04-03
   */
  public static String post(CloseableHttpClient client, HttpParams httpParams) {
    if (httpParams == null) {
      return null;
    }
    CloseableHttpResponse closeableHttpResponse = null;
    // 创建httpclient工具对象
    // CloseableHttpClient client = HttpClients.createDefault();
    // 初始化返回结果
    String responseString = null;
    try {
      // 默认utf-8
      if (StringUtils.isEmpty(httpParams.getCharset())) {
        httpParams.setCharset(StandardCharsets.UTF_8.toString());
      }

      // 创建post请求方法
      HttpPost httpPost = new HttpPost(httpParams.getUrl());
      // 设置header
      if (httpParams.getHeaders() != null) {
        for (Map.Entry<String, String> entry : httpParams.getHeaders().entrySet()) {
          httpPost.addHeader(entry.getKey(), entry.getValue());
        }
      }
      // 装配post请求参数
      if (httpParams.getParams() != null) {
        List<BasicNameValuePair> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : httpParams.getParams().entrySet()) {
          // 请求参数
          list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, httpParams.getCharset());
        // 设置post求情参数
        httpPost.setEntity(entity);
      }
      // post body
      if (!StringUtils.isEmpty(httpParams.getContent())
          && !StringUtils.isEmpty(httpParams.getMimeType())) {
        // 设置参数
        httpPost.setEntity(
            new StringEntity(
                httpParams.getContent(),
                ContentType.create(httpParams.getMimeType(), httpParams.getCharset())));
      }

      RequestConfig requestConfig = getRequestConfig(httpParams);

      httpPost.setConfig(requestConfig);

      closeableHttpResponse = client.execute(httpPost);
      if (closeableHttpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
        // 读取内容
        responseString =
            EntityUtils.toString(closeableHttpResponse.getEntity(), httpParams.getCharset());
      }
    } catch (Exception e) {

      log.error("Post请求出现异常", e);
      throw new RuntimeException("Post请求出现异常", e);

    } finally {
      // 关闭
      if (closeableHttpResponse != null) {
        try {
          closeableHttpResponse.close();
        } catch (IOException e) {
          log.error("Post请求关闭closeableHttpResponse出现异常", e);
        }
      }
    }
    return responseString;
  }

  private static RequestConfig getRequestConfig(HttpParams httpParams) {
    return RequestConfig.custom()
        .setSocketTimeout(
            httpParams.getSocketTimeout() == null ? 2000 : httpParams.getSocketTimeout())
        .setConnectTimeout(
            httpParams.getConnectTimeout() == null ? 2000 : httpParams.getConnectTimeout())
        .setConnectionRequestTimeout(
            httpParams.getConnectionRequestTimeout() == null
                ? 2000
                : httpParams.getConnectionRequestTimeout())
        .build();
  }
}
