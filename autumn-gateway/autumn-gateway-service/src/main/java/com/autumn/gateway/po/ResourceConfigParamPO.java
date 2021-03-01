package com.autumn.gateway.po;

import com.autumn.gateway.entity.ConfigParam;
import com.autumn.gateway.entity.ResourceConfigParam;
import lombok.Data;

/**
 * @program: airlook-dev
 * @description:
 * @author: qius
 * @create: 2021-01-08:10:52
 */
@Data
public class ResourceConfigParamPO extends ResourceConfigParam {

  private ConfigParam configParam;
}
