package com.autumn.gateway.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @program: autumn
 * @description: guava缓存配置
 * @author: qius
 * @create: 2021-03-03:16:30
 */
@Configuration
public class GuavaCacheConfig {

  @Bean("strCache")
  public Cache<String, String> strCache() {
    // 通过CacheBuilder构建一个缓存实例
    Cache<String, String> cache =
        CacheBuilder.newBuilder()
            // 设置缓存的最大容量
            .maximumSize(1000)
            // 设置缓存在写入十分钟后失效
            .expireAfterWrite(10, TimeUnit.MINUTES)
            // 设置并发级别为10
            .concurrencyLevel(10)
            // 开启缓存统计
            .recordStats()
            .build();
    return cache;
  }
}
