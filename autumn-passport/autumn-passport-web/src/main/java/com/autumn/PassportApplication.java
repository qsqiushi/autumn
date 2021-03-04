package com.autumn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @program: autumn
 * @description:
 * @author: qius
 * @create: 2021-03-03:10:56
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.autumn")
// @EnableTransactionManagement
@SpringBootApplication(scanBasePackages = "com.autumn")
@EnableHystrix
public class PassportApplication {

  public static void main(String[] args) {
    SpringApplication.run(PassportApplication.class, args);
  }
}
