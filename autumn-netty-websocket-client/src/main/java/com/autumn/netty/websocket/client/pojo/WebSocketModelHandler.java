package com.autumn.netty.websocket.client.pojo;

import com.autumn.netty.websocket.client.NettyWebSocketClient;
import com.autumn.netty.websocket.client.actuator.WebSocketActuatorDefault;
import com.autumn.netty.websocket.client.converter.ContextConverter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty 数据管道处理类
 */
@Slf4j
public class WebSocketModelHandler implements ChannelInboundHandler {

    private volatile String id;
    private WebSocketModelCache webSocketModelCache;
    private ContextConverter contextConverter;
    private WebSocketClientHandshaker webSocketClientHandshaker;

    public WebSocketModelHandler(String id, WebSocketClientHandshaker webSocketClientHandshaker,
        WebSocketModelCache webSocketModelCache, ContextConverter contextConverter) {
        this.webSocketModelCache = webSocketModelCache;
        this.contextConverter = contextConverter == null ? new WebSocketActuatorDefault() : contextConverter;
        this.webSocketClientHandshaker = webSocketClientHandshaker;
        this.id = id;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        log.info("( " + id + " ) [ channelRegistered ] ==> " + ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        log.info("( " + id + " ) [ channelUnregistered ] ==> " + ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        try {
            log.info("( " + id + " ) [ channelActive ] ==> " + ctx);
            webSocketClientHandshaker.handshake(ctx.channel());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("( " + id + " ) [ channelInactive ] ==> " + ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("( " + id + " ) [ channelRead ] ==> " + ctx);
        try {
            Channel channel = ctx.channel();
            if (!webSocketClientHandshaker.isHandshakeComplete()) {
                // 判断是否完成握手
                try {
                    // 结束握手
                    webSocketClientHandshaker.finishHandshake(channel, (FullHttpResponse)msg);
                    // 客户端生成唯一ID
                    // id = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
                    // 创建 NettyWebSocketClient
                    NettyWebSocketClient nettyWebSocketClient =
                        new NettyWebSocketClient(id, ctx, webSocketClientHandshaker.uri().toString());
                    webSocketModelCache.put(id, nettyWebSocketClient);
                    if (id != null) {
                        // 初始化
                        contextConverter.converterOnOpen(nettyWebSocketClient);
                    } else {
                        log.error("[ CLIENT ID ] ==> NULL.");
                    }
                } catch (WebSocketHandshakeException e) {
                    e.printStackTrace();
                }
            } else if (msg instanceof FullHttpResponse) {
                // 判断是否为 HTTP 返回的状态码
                FullHttpResponse response = (FullHttpResponse)msg;
                if (id != null) {
                    NettyWebSocketClient webSocket = webSocketModelCache.get(id);
                    webSocketModelCache.del(id);
                    contextConverter.converterOnError(webSocket, new Exception("Unexpected FullHttpResponse [ "
                        + response.status() + " ] ==> " + response.content().toString(CharsetUtil.UTF_8)));
                    contextConverter.converterOnClose(webSocket);
                } else {
                    log.error("[ CLIENT ID ] ==> NULL.");
                }
            } else {
                if (id != null) {
                    NettyWebSocketClient webSocket = webSocketModelCache.get(id);
                    if (webSocket != null) {
                        WebSocketFrame frame = (WebSocketFrame)msg;
                        ByteBuf byteBuf = frame.content();
                        byte[] bytes = new byte[byteBuf.readableBytes()];
                        byteBuf.readBytes(bytes);
                        byteBuf.release();
                        if (frame instanceof BinaryWebSocketFrame) {
                            contextConverter.converterOnMessageBinary(webSocket, bytes);
                        } else if (frame instanceof TextWebSocketFrame) {
                            contextConverter.converterOnMessageText(webSocket, new String(bytes));
                        } else if (frame instanceof PongWebSocketFrame) {
                            contextConverter.converterOnMessagePong(webSocket, bytes);
                        } else if (frame instanceof PingWebSocketFrame) {
                            contextConverter.converterOnMessagePing(webSocket, bytes);
                        } else if (frame instanceof ContinuationWebSocketFrame) {
                            contextConverter.converterOnMessageContinuation(webSocket, bytes);
                        }
                    }
                } else {
                    log.error("[ CLIENT ID ] ==> NULL.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("( " + id + " ) [ channelReadComplete ] ==> " + ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        log.info("( " + id + " ) [ userEventTriggered ] ==> " + ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        log.info("( " + id + " ) [ channelWritabilityChanged ] ==> " + ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info("( " + id + " ) [ handlerAdded ] ==> " + ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        log.info("( " + id + " ) [ handlerRemoved ] ==> " + ctx);
        if (id != null) {
            if (webSocketModelCache.get(id) != null) {
                contextConverter.converterOnClose(webSocketModelCache.get(id));
                webSocketModelCache.del(id);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("( " + id + " ) [ exceptionCaught ] ==> " + ctx);
        cause.printStackTrace();
        if (id != null) {
            contextConverter.converterOnError(webSocketModelCache.get(id), cause);
        }
        ctx.close(); // 关闭管道
    }

}
