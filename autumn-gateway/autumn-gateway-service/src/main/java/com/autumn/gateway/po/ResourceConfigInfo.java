package com.autumn.gateway.po;

import com.autumn.gateway.entity.Config;
import com.autumn.gateway.entity.ResourceConfig;
import lombok.Data;

/**
 * @program: autumn-dev
 * @description:
 * @author: qiushi
 * @create: 2021-01-07:17:17
 */
@Data
public class ResourceConfigInfo extends ResourceConfig {

  private Config config;
}
