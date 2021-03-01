package com.autumn.gateway.service;

import com.autumn.gateway.entity.Resource;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
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
     * @author qius
     * @updator qius
     * @since 2021/1/13 10:49
     */
    void saveConfig(Resource resource, List<Map<String, String>> params);
}
