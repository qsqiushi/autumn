package com.autumn.gateway.enums;

import com.autumn.model.enums.CommonEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @program: map-dev
 * @description: 接触类枚举 是否
 * @author: qiushi
 * @create: 2019-06-17:14:52
 */
@Getter
public enum BaseWhetherEnum implements CommonEnum<BaseWhetherEnum> {
  NO(0, "否"),

  YES(1, "是");
  @EnumValue private final int code;

  private final String name;

  BaseWhetherEnum(int code, String name) {
    this.code = code;
    this.name = name;
  }

  public static BaseWhetherEnum getByCode(int code) {
    for (BaseWhetherEnum temp : BaseWhetherEnum.values()) {
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
