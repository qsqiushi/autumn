package com.autumn.netty.websocket.client.actuator;

import com.autumn.netty.websocket.client.NettyWebSocketClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @program:
 * @description: 模式执行器
 * @author: qius
 * @create: 2020-09-02:19:52
 **/
@Slf4j
public class WebSocketActuatorDefaultAsync extends WebSocketActuatorAbstractAsync {

    @Override
    public void onOpen(NettyWebSocketClient webSocket) {
        log.info("AsyncWebSocket [{}] ==> onOpen.", webSocket.getUrl());
    }

    @Override
    public void onClose(NettyWebSocketClient webSocket) {
        log.info("AsyncWebSocket [{}] ==> onClose.", webSocket.getUrl());
    }

    @Override
    public void onError(NettyWebSocketClient webSocket, Throwable throwable) {
        log.info("AsyncWebSocket [{}] ==> onError. \n throwable => [{}]", webSocket.getUrl(), throwable.getMessage());
    }

    @Override
    public void onMessageText(NettyWebSocketClient webSocket, String message) {
        log.info("AsyncWebSocket [{}] ==> onMessageText. \n message => [{}]", webSocket.getUrl(), message);
    }

    @Override
    public void onMessageBinary(NettyWebSocketClient webSocket, byte[] message) {
        log.info("AsyncWebSocket [{}] ==> onMessageBinary. \n message => [{}]", webSocket.getUrl(), new String(message));
    }

    @Override
    public void onMessagePong(NettyWebSocketClient webSocket, byte[] message) {
        log.info("AsyncWebSocket [{}] ==> onMessagePong. \n message => [{}]", webSocket.getUrl(), new String(message));
    }

    @Override
    public void onMessagePing(NettyWebSocketClient webSocket, byte[] message) {
        log.info("AsyncWebSocket [{}] ==> onMessagePing. \n message => [{}]", webSocket.getUrl(), new String(message));
    }

    @Override
    public void onMessageContinuation(NettyWebSocketClient webSocket, byte[] message) {
        log.info("AsyncWebSocket [{}] ==> onMessageContinuation. \n message => [{}]", webSocket.getUrl(), new String(message));
    }

}
