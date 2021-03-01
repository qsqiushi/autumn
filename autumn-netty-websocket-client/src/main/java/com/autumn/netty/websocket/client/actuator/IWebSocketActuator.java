package com.autumn.netty.websocket.client.actuator;


import com.autumn.netty.websocket.client.NettyWebSocketClient;

/**
 * <长链接客户端执行器>
 *
 * @author qius
 * @since 2020/9/2 17:45
 **/
public interface IWebSocketActuator {
    /**
     * 连接成功的触发的事件
     *
     * @param webSocket WebSocket 对象的封装
     */
    public void onOpen(NettyWebSocketClient webSocket);

    /**
     * 连接关闭的触发的事件
     *
     * @param webSocket WebSocket 对象的封装
     */
    public void onClose(NettyWebSocketClient webSocket);

    /**
     * 连接错误的触发的事件
     *
     * @param webSocket WebSocket 对象的封装
     * @param throwable 错误事件对象
     */
    public void onError(NettyWebSocketClient webSocket, Throwable throwable);


    /**
     * 触发文本消息的事件
     *
     * @param webSocket WebSocket 对象的封装
     * @param message
     */
    public void onMessageText(NettyWebSocketClient webSocket, String message);


    /**
     * 触发二进制消息的事件
     *
     * @param webSocket WebSocket 对象的封装
     * @param message   消息内容
     */
    public void onMessageBinary(NettyWebSocketClient webSocket, byte[] message);

    /**
     * 触发 Pong 消息的事件
     *
     * @param webSocket WebSocket 对象的封装
     * @param message   消息内容
     */
    public void onMessagePong(NettyWebSocketClient webSocket, byte[] message);

    /**
     * 触发 Ping 消息的事件
     *
     * @param webSocket WebSocket 对象的封装
     * @param message   消息内容
     */
    public void onMessagePing(NettyWebSocketClient webSocket, byte[] message);

    /**
     * 触发 Continuation 消息的事件
     *
     * @param webSocket WebSocket 对象的封装
     * @param message   消息内容
     */
    public void onMessageContinuation(NettyWebSocketClient webSocket, byte[] message);


}
