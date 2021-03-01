package com.autumn.netty.websocket.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @program:
 * @description: netty客户端
 * @author: qius
 * @create: 2020-09-02:17:12
 **/
public class NettyWebSocketClient {

    private String id;

    private ChannelHandlerContext channelHandlerContext;

    private String url;

    public NettyWebSocketClient(String id, ChannelHandlerContext channelHandlerContext) {
        this.id = id;
        this.channelHandlerContext = channelHandlerContext;
    }

    public NettyWebSocketClient(String id, ChannelHandlerContext channelHandlerContext, String url) {
        this.id = id;
        this.channelHandlerContext = channelHandlerContext;
        this.url = url;
    }

    public void sendTextMessage(String message) {
        this.channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(message));
    }

    public void sendBinaryMessage(byte[] message) {
        this.channelHandlerContext.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(message)));
    }

    public void sendPingMessage(byte[] message) {
        this.channelHandlerContext.channel().writeAndFlush(new PingWebSocketFrame(Unpooled.wrappedBuffer(message)));
    }

    public void sendPongMessage(byte[] message) {
        this.channelHandlerContext.channel().writeAndFlush(new PongWebSocketFrame(Unpooled.wrappedBuffer(message)));
    }

    public void sendContinuationMessage(byte[] message) {
        this.channelHandlerContext.channel().writeAndFlush(new ContinuationWebSocketFrame(Unpooled.wrappedBuffer(message)));
    }

    public void close() {
        channelHandlerContext.channel().close();
    }

    public boolean isOpen() {
        return channelHandlerContext.channel().isOpen();
    }

    public boolean isActive() {
        return channelHandlerContext.channel().isActive();
    }

    public boolean isRegistered() {
        return channelHandlerContext.channel().isRegistered();
    }

    public boolean isWritable() {
        return channelHandlerContext.channel().isWritable();
    }

    public Channel getChannel() {
        return channelHandlerContext.channel();
    }

    public String getId() {
        return id;
    }


    public String getUrl() {
        return url;
    }


    public void heartCheck() {

        channelHandlerContext.pipeline().addLast("heartCheck", new IdleStateHandler(25, 15, 10, TimeUnit.SECONDS));

    }
}
