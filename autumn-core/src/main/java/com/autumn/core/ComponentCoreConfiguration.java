package com.autumn.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

/**
 * @author Aysn
 * @since 2019-06-17 15:46
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@Validated
@Configuration
@ComponentScan(basePackages = com.autumn.core.Constants.BASE_PACKAGE)
@EnableConfigurationProperties(com.autumn.core.ComponentCoreProperties.class)
public class ComponentCoreConfiguration {

  @NotNull(message = "spring.application.name is required")
  @Value("${spring.application.name}")
  private String applicationName;

  @PostConstruct
  public void init() {
    log.info("Loading AirLook Component Core Configuration");
  }
}
