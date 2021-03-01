package com.autumn.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * @author Aysn
 * @since 2019-05-27 15:46
 */
@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "autumn.component")
public class ComponentCoreProperties {

  @NotNull(message = "请设置顶级包路径，例如：com.autumn")
  private String basePackage;
}
