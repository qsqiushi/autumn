package com.autumn.netty.websocket.client.actuator;

import com.autumn.netty.websocket.client.NettyWebSocketClient;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @program:
 * @description:
 * @author: qiushi
 * @create: 2020-09-02:19:48
 */
public abstract class WebSocketActuatorAbstractAsync extends WebSocketActuatorBase {

  /**
   * 创建一个默认的线程池，大小为 30 个线程
   *
   * <p>所有的异步线程的共用一个线程池
   */
  protected static ThreadPoolExecutor threadPool = null;

  public static void init(ThreadPoolExecutor tp) {
    threadPool = tp;
  }

  @Override
  public void converterOnOpen(NettyWebSocketClient websocket) {
    if (threadPool == null || threadPool.isShutdown()) {
      return;
    }
    threadPool.execute(
        () -> {
          this.onOpen(websocket);
          System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
        });
  }

  @Override
  public void converterOnClose(NettyWebSocketClient websocket) {
    if (threadPool == null || threadPool.isShutdown()) {
      return;
    }
    threadPool.execute(
        () -> {
          this.onClose(websocket);
          System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
        });
  }

  @Override
  public void converterOnError(NettyWebSocketClient websocket, Throwable throwable) {
    if (threadPool == null || threadPool.isShutdown()) {
      return;
    }
    threadPool.execute(
        () -> {
          this.onOpen(websocket);
          System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
        });
  }

  @Override
  public void converterOnMessageText(NettyWebSocketClient websocket, String message) {
    if (threadPool == null || threadPool.isShutdown()) {
      return;
    }
    threadPool.execute(
        () -> {
          this.onMessageText(websocket, message);
          //System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
        });
  }

  @Override
  public void converterOnMessageBinary(NettyWebSocketClient websocket, byte[] message) {
    if (threadPool == null || threadPool.isShutdown()) {
      return;
    }
    threadPool.execute(
        () -> {
          this.onMessageBinary(websocket, message);
          System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
        });
  }

  @Override
  public void converterOnMessagePong(NettyWebSocketClient websocket, byte[] message) {
    if (threadPool == null || threadPool.isShutdown()) {
      return;
    }
    threadPool.execute(
        () -> {
          this.onMessagePong(websocket, message);
          System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
        });
  }

  @Override
  public void converterOnMessagePing(NettyWebSocketClient websocket, byte[] message) {
    if (threadPool == null || threadPool.isShutdown()) {
      return;
    }
    threadPool.execute(
        () -> {
          this.onMessagePing(websocket, message);
          System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
        });
  }

  @Override
  public void converterOnMessageContinuation(NettyWebSocketClient websocket, byte[] message) {
    if (threadPool == null || threadPool.isShutdown()) {
      return;
    }
    threadPool.execute(
        () -> {
          this.onMessageContinuation(websocket, message);
          System.gc(); // 回收掉生成的一次性的对象，避免内存泄漏
        });
  }

  @Override
  public String toType() {
    return "ASYNCHRONOUS_TYPE";
  }
}
