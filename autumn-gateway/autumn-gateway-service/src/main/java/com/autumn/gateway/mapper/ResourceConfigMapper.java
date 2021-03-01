package com.autumn.gateway.mapper;

import com.autumn.gateway.entity.ResourceConfig;
import com.autumn.gateway.po.ResourceConfigInfo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 自动生成
 * @since 2021-01-07
 */
@Mapper
public interface ResourceConfigMapper extends BaseMapper<ResourceConfig> {

    /**
     * <查询资源配置>
     *
     * @param queryWrapper
     * @return : java.util.List<com.airlook.gateway.po.ResourceConfigInfo>
     * @author qius
     * @updator qius
     * @since 2021/1/7 17:25
     */
    List<ResourceConfigInfo> selectResourceConfigInfo(@Param(Constants.WRAPPER) Wrapper<ResourceConfig> queryWrapper);

}
