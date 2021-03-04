package com.autumn.gateway.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * <配置类型>
 *
 * @author qiushi
 * @since 2021/1/7 19:22
 */
@Getter
public enum ConfigTypeEnum {
  FILTER("FILTER", "过滤器"),

  PREDICATE("PREDICATE", "断言"),

  GATEWAY_FLOW_RULE("GATEWAY_FLOW_RULE", "限流规则"),

  DEGRADE_RULE("DEGRADE_RULE", "熔断规则"),

  AUTHORITY_RULE("AUTHORITY_RULE", "黑白名单"),
  ;

  @EnumValue private final String code;

  private final String name;

  ConfigTypeEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public static ConfigTypeEnum getByCode(String code) {
    for (ConfigTypeEnum temp : ConfigTypeEnum.values()) {
      if (StringUtils.equals(temp.getCode(), code)) {
        return temp;
      }
    }
    return null;
  }
}
