package com.autumn.netty.websocket.client.actuator;


import com.autumn.netty.websocket.client.converter.ContextConverter;

/**
 * @program:
 * @description: 基础执行类
 * @author: qius
 * @create: 2020-09-02:19:30
 **/
public abstract class WebSocketActuatorBase implements ContextConverter, IWebSocketActuator {
    /**
     * 返回当前实现的类型
     *
     * @return 类型数据
     */
    public abstract String toType();
}
