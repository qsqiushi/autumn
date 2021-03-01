package com.autumn.gateway.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author 自动生成
 * @since 2021-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Resource extends BasePO {

    private static final long serialVersionUID = 1L;

    /**
     * 网关URI，LB://TEST
     */
    @TableField("URI")
    private String uri;
    /**
     * 请求路径 /api/test
     */
    @TableField("URL")
    private String url;

    /**
     * 组ID
     */
    @TableField("GROUP_ID")
    private String groupId;


}
