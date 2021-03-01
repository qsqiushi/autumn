package com.autumn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * <启动类>
 *
 * @author qius
 * @since 2020/12/23 19:24
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class GatewayApplication {

  public static void main(String[] args) {
    System.setProperty("csp.sentinel.app.type", "1");
    SpringApplication.run(GatewayApplication.class, args);
  }
}
