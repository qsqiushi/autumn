package com.autumn.gateway.service;

import com.autumn.gateway.entity.Resource;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 服务类
 *
 * @author 自动生成
 * @since 2021-01-07
 */
public interface IResourceService extends IService<Resource> {
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
  void saveConfig(Resource resource, List<Map<String, String>> params);
}
