package com.autumn.gateway.mapper;

import com.autumn.gateway.entity.ResourceConfigParam;
import com.autumn.gateway.po.ResourceConfigParamPO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper 接口
 *
 * @author 自动生成
 * @since 2021-01-07
 */
@Mapper
public interface ResourceConfigParamMapper extends BaseMapper<ResourceConfigParam> {
  /**
   * <>
   *
   * @param queryWrapper
   * @return : java.util.List
   * @author qiushi
   * @updator qiushi
   * @since 2021/1/8 11:01
   */
  List<ResourceConfigParamPO> selectResourceConfigParamInfo(
      @Param(Constants.WRAPPER) Wrapper<ResourceConfigParam> queryWrapper);
}
