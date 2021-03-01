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
public class ResourceGroup extends BasePO {

  private static final long serialVersionUID = 1L;

  /** 名称 */
  @TableField("NAME")
  private String name;

  @TableField("CODE")
  private String code;

  /** 域名 */
  @TableField("DOMAIN")
  private String domain;
}
