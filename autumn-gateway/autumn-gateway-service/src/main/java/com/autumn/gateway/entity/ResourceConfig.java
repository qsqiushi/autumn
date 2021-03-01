package com.autumn.gateway.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author 自动生成
 * @since 2021-01-07
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class ResourceConfig extends BasePO {

  private static final long serialVersionUID = 1L;

  @TableField("CONFIG_ID")
  private String configId;

  @TableField("RESOURCE_ID")
  private String resourceId;
}
