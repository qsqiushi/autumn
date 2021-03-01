package com.autumn.netty.websocket.support;

import com.autumn.netty.websocket.pojo.Session;
import com.autumn.netty.websocket.pojo.PojoEndpointServer;
import io.netty.channel.Channel;
import org.springframework.core.MethodParameter;

public class SessionMethodArgumentResolver implements MethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Session.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Channel channel, Object object) throws Exception {
        Session session = channel.attr(PojoEndpointServer.SESSION_KEY).get();
        return session;
    }
}
