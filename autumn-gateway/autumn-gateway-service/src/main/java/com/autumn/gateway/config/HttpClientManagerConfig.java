package com.autumn.gateway.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @program: dataearth-cloud-dev
 * @description: 连接池配置
 * @author: qius
 * @create: 2019-04-02 16:17
 */
@Component
@Configuration
public class HttpClientManagerConfig {
  @Bean(name = "poolingHttpClientConnectionManager")
  public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
    PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
        new PoolingHttpClientConnectionManager();
    poolingHttpClientConnectionManager.setMaxTotal(200);
    poolingHttpClientConnectionManager.setDefaultMaxPerRoute(50);
    return poolingHttpClientConnectionManager;
  }

  @Bean(name = "httpClientBuilder")
  public HttpClientBuilder httpClientBuilder() {
    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
    httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager());
    return httpClientBuilder;
  }

  @Bean(name = "httpClient")
  public HttpClient httpClient() {
    return httpClientBuilder().build();
  }
}
