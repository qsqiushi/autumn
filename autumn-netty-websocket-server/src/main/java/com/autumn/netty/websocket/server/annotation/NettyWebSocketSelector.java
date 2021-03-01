package com.autumn.netty.websocket.server.annotation;

import com.autumn.netty.websocket.server.standard.ServerEndpointExporter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean(ServerEndpointExporter.class)
@Configuration
public class NettyWebSocketSelector {

  @Bean
  public ServerEndpointExporter serverEndpointExporter() {
    return new ServerEndpointExporter();
  }
}
