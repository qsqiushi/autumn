package com.autumn.netty.websocket.client.converter;

import com.autumn.netty.websocket.client.NettyWebSocketClient;

/**
 * @program:
 * @description: 上下文转换器 将 Netty 触发的事件转换触发自定义 Web Socket Client 事件
 * @author: qiushi
 * @create: 2020-09-02:19:31
 */
public interface ContextConverter {

  /**
   * 连接触发的回调
   *
   * @param websocket 对 Netty 封装的对象
   */
  public void converterOnOpen(NettyWebSocketClient websocket);

  /**
   * 关闭触发的回调
   *
   * @param websocket 对 Netty 封装的对象
   */
  public void converterOnClose(NettyWebSocketClient websocket);

  /**
   * 错误触发的回调
   *
   * @param websocket 对 Netty 封装的对象
   */
  public void converterOnError(NettyWebSocketClient websocket, Throwable throwable);

  /**
   * 文本消息触发的回调
   *
   * @param websocket 对 Netty 封装的对象
   */
  public void converterOnMessageText(NettyWebSocketClient websocket, String message);

  /**
   * 二进制流消息触发的回调
   *
   * @param websocket 对 Netty 封装的对象
   */
  public void converterOnMessageBinary(NettyWebSocketClient websocket, byte[] message);

  /**
   * Pong 消息触发的回调
   *
   * @param websocket 对 Netty 封装的对象
   */
  public void converterOnMessagePong(NettyWebSocketClient websocket, byte[] message);

  /**
   * Ping 消息连接触发的回调
   *
   * @param websocket 对 Netty 封装的对象
   */
  public void converterOnMessagePing(NettyWebSocketClient websocket, byte[] message);

  /**
   * Continuation 消息触发的回调
   *
   * @param websocket 对 Netty 封装的对象
   */
  public void converterOnMessageContinuation(NettyWebSocketClient websocket, byte[] message);
}
