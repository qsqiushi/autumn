package com.autumn.gateway.common;

import org.springframework.util.AntPathMatcher;

/**
 * @program: dataearth-online-cloud
 * @description: 常量类
 * @author: qiushi
 * @create: 2019-01-31 16:55
 */
public class GateWayConstants {


    /**
     * open api 鉴权头
     */
    public static final String OPEN_API_HEADER_AUTHORIZATION = "Authorization";

    /**
     * open api requestBody 缓存名称
     */
    public static final String OPEN_API_ATTRIBUTE_CACHED_REQUEST_BODY_OBJECT = "cachedRequestBodyObject";

    /**
     * open api 请求 有效时长 毫秒
     */
    public static final Long OPEN_API_REQUEST_ACTIVE_TIME = new Long(1000 * 60 * 5);

}
