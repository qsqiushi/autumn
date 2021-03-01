package com.autumn.gateway.service;

import com.autumn.gateway.entity.Resource;

public interface IDynamicRouteService {

  String delete(String id);

  /**
   * <更新路由>
   *
   * @param resource
   * @return : void
   * @author qiushi
   * @updator qiushi
   * @since 2021/1/15 10:56
   */
  void updateResource(Resource resource);

  void addResource(Resource resource);
}
