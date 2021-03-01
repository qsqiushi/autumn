package com.autumn.gateway.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 自动生成
 * @since 2021-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ConfigParam extends BasePO {

  private static final long serialVersionUID = 1L;

  @TableField("CODE")
  private String code;

  @TableField("NAME")
  private String name;

  @TableField("CONFIG_ID")
  private String configId;
}
