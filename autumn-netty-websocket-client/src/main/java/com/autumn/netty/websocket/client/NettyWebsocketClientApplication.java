package com.autumn.netty.websocket.client;

import com.autumn.netty.websocket.client.actuator.WebSocketActuatorAbstractAsync;
import com.autumn.netty.websocket.client.config.WebSocketConfig;
import com.autumn.netty.websocket.client.pojo.WebSocketModel;
import com.autumn.netty.websocket.client.pojo.WebSocketModelCache;
import com.autumn.netty.websocket.client.pojo.WebSocketModelNio;

import java.util.UUID;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Netty Web Socket 应用类 */
public class NettyWebsocketClientApplication {
  private WebSocketModel webSocketModel;
  private ThreadPoolExecutor threadPool;

  public static NettyWebsocketClientApplication run() {
    return new NettyWebsocketClientApplication(WebSocketModelNio.class, new WebSocketConfig[] {});
  }

  public static NettyWebsocketClientApplication run(WebSocketModel model) {
    return new NettyWebsocketClientApplication(model, new WebSocketConfig[] {});
  }

  public static NettyWebsocketClientApplication run(Class<? extends WebSocketModel> model) {
    return new NettyWebsocketClientApplication(model, new WebSocketConfig[] {});
  }

  public static NettyWebsocketClientApplication run(
      WebSocketModel model, WebSocketConfig... products) {
    return new NettyWebsocketClientApplication(model, products);
  }

  public static NettyWebsocketClientApplication run(
      Class<? extends WebSocketModel> model, WebSocketConfig... products) {
    return new NettyWebsocketClientApplication(model, products);
  }

  public static NettyWebsocketClientApplication run(
      WebSocketModel model, ThreadPoolExecutor threadPool) {
    return new NettyWebsocketClientApplication(model, threadPool, new WebSocketConfig[] {});
  }

  public static NettyWebsocketClientApplication run(
      Class<? extends WebSocketModel> model, ThreadPoolExecutor threadPool) {
    return new NettyWebsocketClientApplication(model, threadPool, new WebSocketConfig[] {});
  }

  public static NettyWebsocketClientApplication run(
      WebSocketModel model, ThreadPoolExecutor threadPool, WebSocketConfig... products) {
    return new NettyWebsocketClientApplication(model, threadPool, products);
  }

  public static NettyWebsocketClientApplication run(
      Class<? extends WebSocketModel> model,
      ThreadPoolExecutor threadPool,
      WebSocketConfig... products) {
    return new NettyWebsocketClientApplication(model, threadPool, products);
  }

  public NettyWebsocketClientApplication(
      Class<? extends WebSocketModel> model, WebSocketConfig[] products) {
    try {
      this.threadPool =
          new ThreadPoolExecutor(0, 30, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
      this.webSocketModel = model.newInstance().run().connect(products);
      WebSocketActuatorAbstractAsync.init(threadPool);
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public NettyWebsocketClientApplication(WebSocketModel model, WebSocketConfig[] products) {
    this.threadPool =
        new ThreadPoolExecutor(0, 30, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    this.webSocketModel = model.connect(products);
    WebSocketActuatorAbstractAsync.init(threadPool);
  }

  public NettyWebsocketClientApplication(
      Class<? extends WebSocketModel> model,
      ThreadPoolExecutor threadPool,
      WebSocketConfig[] products) {
    try {
      this.threadPool = threadPool;
      this.webSocketModel = model.newInstance().run().connect(products);
      WebSocketActuatorAbstractAsync.init(threadPool);
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public NettyWebsocketClientApplication(
      WebSocketModel model, ThreadPoolExecutor threadPool, WebSocketConfig[] products) {
    this.threadPool = threadPool;
    this.webSocketModel = model.connect(products);
    WebSocketActuatorAbstractAsync.init(threadPool);
  }

  public NettyWebsocketClientApplication connect(String id, WebSocketConfig product) {
    this.webSocketModel.connect(id, product);
    return this;
  }

  public NettyWebsocketClientApplication connect(WebSocketConfig product) {
    this.webSocketModel.connect(UUID.randomUUID().toString(), product);
    return this;
  }

  public NettyWebsocketClientApplication connect(WebSocketConfig[] products) {
    this.webSocketModel.connect(products);
    return this;
  }

  public NettyWebsocketClientApplication close() {
    this.webSocketModel.close();
    return this;
  }

  public NettyWebsocketClientApplication close(String id) {
    this.webSocketModel.close(id);
    return this;
  }

  public NettyWebsocketClientApplication close(String[] ids) {
    this.webSocketModel.close(ids);
    return this;
  }

  public void destroy() {
    this.webSocketModel.close();
    if (threadPool != null && !threadPool.isShutdown()) {
      threadPool.shutdown();
    }
  }

  public WebSocketModelCache getWebSocketModelCache() {
    return this.webSocketModel.getWebSocketModelCache();
  }

  public ThreadPoolExecutor getThreadPool() {
    return threadPool;
  }
}
