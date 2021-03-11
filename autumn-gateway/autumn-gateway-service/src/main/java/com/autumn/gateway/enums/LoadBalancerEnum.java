package com.autumn.gateway.enums;

import lombok.Getter;

/**
 * @program: autumn
 * @description:
 * @author: qiushi
 * @create: 2021-03-10:15:14
 */
@Getter
public enum LoadBalancerEnum {
  VERSION("版本"),

  WEIGHT("权重");

  private final String desc;

  LoadBalancerEnum(String desc) {

    this.desc = desc;
  }
}
