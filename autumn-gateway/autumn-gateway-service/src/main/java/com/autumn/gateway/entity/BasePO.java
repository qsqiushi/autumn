package com.autumn.gateway.entity;

import com.autumn.gateway.enums.BaseStatus;
import com.autumn.gateway.enums.BaseWhether;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author qiushi
 * @date 2017年1月13日 下午1:18:11
 * @describe 基类
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BasePO implements Serializable {

  /** 序列化 */
  private static final long serialVersionUID = 1L;
  /** 主键，自增 */
  @TableId(value = "ID", type = IdType.ASSIGN_ID)
  private String id;
  /** 创建人 */
  @TableField(value = "CREATE_BY")
  private String createBy;
  /** 创建时间 */
  @TableField("CREATE_TIME")
  private LocalDateTime createTime;
  /** 修改人 */
  @TableField("UPDATE_BY")
  private String updateBy;
  /** 修改时间 */
  @TableField("UPDATE_TIME")
  private LocalDateTime updateTime;
  /**
   * 逻辑删除状态
   *
   * <p>可选注解@TableLogic(value ="0",delval = "1")
   */
  @EnumValue
  @TableField("DELETED")
  private BaseWhether deleted;

  /** 状态 */
  @EnumValue
  @TableField("STATUS")
  private BaseStatus status;

  @Version
  @TableField("VERSION")
  private Integer version;

  /** 备注 */
  @TableField("REMARK")
  private String remark;
}
