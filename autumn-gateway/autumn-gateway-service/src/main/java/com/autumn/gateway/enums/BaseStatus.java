package com.autumn.gateway.enums;

import com.autumn.model.enums.CommonEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @program: map-dev
 * @description: 基础类状态枚举
 * @author: qiushi
 * @create: 2019-06-17:14:32
 */
@Getter
public enum BaseStatus implements CommonEnum<BaseStatus> {
  VALID(0, "有效"),

  INVALID(1, "无效");
  @EnumValue private final int code;

  private final String name;

  BaseStatus(int code, String name) {
    this.code = code;
    this.name = name;
  }

  public static BaseStatus getByCode(int code) {
    for (BaseStatus temp : BaseStatus.values()) {
      if (temp.getCode() == code) {
        return temp;
      }
    }
    return null;
  }

  @Override
  public int getCode() {
    return code;
  }
}
