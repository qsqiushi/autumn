package com.autumn.gateway.entity;

import com.autumn.gateway.enums.ConfigType;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 自动生成
 * @since 2021-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Config extends BasePO {

  private static final long serialVersionUID = 1L;

  @TableField("NAME")
  private String name;

  @TableField("CODE")
  private String code;

  /** PREDICATE 和 FILTER */
  @EnumValue
  @TableField("TYPE")
  private ConfigType type;
}
