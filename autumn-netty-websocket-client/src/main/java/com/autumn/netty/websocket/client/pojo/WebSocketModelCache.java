package com.autumn.netty.websocket.client.pojo;

import com.autumn.netty.websocket.client.NettyWebSocketClient;

import java.util.HashMap;
import java.util.Map;

/**
 * 每一个模板对应一个缓存对象，用来缓存连接的客户端信息
 */
public class WebSocketModelCache {

    private Map<String, NettyWebSocketClient> nettyWebSocketClientHashMap = new HashMap<>();

    public void put(String id, NettyWebSocketClient client) {
        if (id != null) {
            nettyWebSocketClientHashMap.put(id, client);
        }
    }

    public void del(String id) {
        if (id != null) {
            nettyWebSocketClientHashMap.remove(id);
        }
    }

    public NettyWebSocketClient get(String id) {
        if (id == null) {
            return null;
        } else {
            return nettyWebSocketClientHashMap.get(id);
        }
    }

    public Map<String, NettyWebSocketClient> get() {
        return nettyWebSocketClientHashMap;
    }

}
