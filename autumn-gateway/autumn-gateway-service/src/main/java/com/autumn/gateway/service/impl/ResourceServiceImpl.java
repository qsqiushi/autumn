package com.autumn.gateway.service.impl;

import com.autumn.gateway.entity.*;
import com.autumn.gateway.enums.BaseStatus;
import com.autumn.gateway.enums.BaseWhether;
import com.autumn.gateway.enums.ConfigType;
import com.autumn.gateway.mapper.*;
import com.autumn.gateway.service.IResourceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 服务实现类
 *
 * @author 自动生成
 * @since 2021-01-07
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource>
    implements IResourceService {

  @javax.annotation.Resource private ConfigMapper configMapper;

  @javax.annotation.Resource private ResourceConfigMapper resourceConfigMapper;

  @javax.annotation.Resource private ConfigParamMapper configParamMapper;

  @javax.annotation.Resource private ResourceConfigParamMapper resourceConfigParamMapper;

  /**
   * <保存资源的配置>
   *
   * @param resource
   * @param params
   * @return : void
   * @author qiushi
   * @updator qiushi
   * @since 2021/1/13 10:49
   */
  @Override
  public void saveConfig(Resource resource, List<Map<String, String>> params) {

    for (Map<String, String> param : params) {

      ConfigType configType = ConfigType.getByCode(param.get("configType"));

      if (configType == null) {
        continue;
      }

      Config config =
          configMapper.selectOne(
              new QueryWrapper<Config>()
                  .lambda()
                  .eq(Config::getType, configType)
                  .eq(Config::getCode, param.get("configCode"))
                  .eq(Config::getStatus, BaseStatus.VALID)
                  .eq(Config::getDeleted, BaseWhether.NO));

      if (config == null) {
        continue;
      }

      ResourceConfig resourceConfig =
          resourceConfigMapper.selectOne(
              new QueryWrapper<ResourceConfig>()
                  .lambda()
                  .eq(ResourceConfig::getStatus, BaseStatus.VALID)
                  .eq(ResourceConfig::getDeleted, BaseWhether.NO)
                  .eq(ResourceConfig::getConfigId, config.getId())
                  .eq(ResourceConfig::getResourceId, resource.getId()));

      if (resourceConfig == null) {
        resourceConfig =
            new ResourceConfig().setConfigId(config.getId()).setResourceId(resource.getId());
        resourceConfigMapper.insert(resourceConfig);
      }

      Iterator<Map.Entry<String, String>> entries = param.entrySet().iterator();
      while (entries.hasNext()) {
        Map.Entry<String, String> entry = entries.next();
        String key = entry.getKey();
        String value = entry.getValue();

        if (StringUtils.equals(key, "configType") || StringUtils.equals(key, "configCode")) {
          continue;
        }

        ConfigParam configParam =
            configParamMapper.selectOne(
                new QueryWrapper<ConfigParam>()
                    .lambda()
                    .eq(ConfigParam::getCode, key)
                    .eq(ConfigParam::getConfigId, config.getId())
                    .eq(ConfigParam::getStatus, BaseStatus.VALID)
                    .eq(ConfigParam::getDeleted, BaseWhether.NO));
        if (configParam == null) {
          continue;
        }

        ResourceConfigParam resourceConfigParam =
            resourceConfigParamMapper.selectOne(
                new QueryWrapper<ResourceConfigParam>()
                    .lambda()
                    .eq(ResourceConfigParam::getStatus, BaseStatus.VALID)
                    .eq(ResourceConfigParam::getDeleted, BaseWhether.NO)
                    .eq(ResourceConfigParam::getConfigParamId, configParam.getId())
                    .eq(ResourceConfigParam::getResourceConfigId, resourceConfig.getId()));

        if (resourceConfigParam == null) {
          resourceConfigParam = new ResourceConfigParam();

          resourceConfigParam
              .setConfigParamId(configParam.getId())
              .setResourceConfigId(resourceConfig.getId())
              .setValue(value);

          resourceConfigParamMapper.insert(resourceConfigParam);
        } else {
          resourceConfigParam.setValue(value);
          resourceConfigParamMapper.updateById(resourceConfigParam);
        }
      }
    }
  }
}
