package com.autumn.gateway.po;

import com.autumn.gateway.entity.Config;
import com.autumn.gateway.entity.ResourceConfig;
import lombok.Data;

/**
 * @program: airlook-dev
 * @description:
 * @author: qius
 * @create: 2021-01-07:17:17
 */
@Data
public class ResourceConfigInfo extends ResourceConfig {

  private Config config;
}
