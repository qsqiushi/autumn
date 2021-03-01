package com.autumn.netty.websocket.client.actuator;

import com.autumn.netty.websocket.client.NettyWebSocketClient;

/**
 * @program:
 * @description:
 * @author: qiushi
 * @create: 2020-09-02:19:29
 */
public abstract class WebSocketActuatorAbstract extends WebSocketActuatorBase {

  @Override
  public void converterOnOpen(NettyWebSocketClient websocket) {
    this.onOpen(websocket);
    System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
  }

  @Override
  public void converterOnClose(NettyWebSocketClient websocket) {
    this.onClose(websocket);
    System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
  }

  @Override
  public void converterOnError(NettyWebSocketClient websocket, Throwable throwable) {
    this.onError(websocket, throwable);
    System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
  }

  @Override
  public void converterOnMessageText(NettyWebSocketClient websocket, String message) {
    this.onMessageText(websocket, message);
    System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
    System.out.println("gc once");
  }

  @Override
  public void converterOnMessageBinary(NettyWebSocketClient websocket, byte[] message) {
    this.onMessageBinary(websocket, message);
    System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
  }

  @Override
  public void converterOnMessagePong(NettyWebSocketClient websocket, byte[] message) {
    this.onMessagePong(websocket, message);
    System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
  }

  @Override
  public void converterOnMessagePing(NettyWebSocketClient websocket, byte[] message) {
    this.onMessagePing(websocket, message);
    System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
  }

  @Override
  public void converterOnMessageContinuation(NettyWebSocketClient websocket, byte[] message) {
    this.onMessageContinuation(websocket, message);
    System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
  }

  @Override
  public String toType() {
    return "SYNCHRONIZATION_TYPE";
  }
}
