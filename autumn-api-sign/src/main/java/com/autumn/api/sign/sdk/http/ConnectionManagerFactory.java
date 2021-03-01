//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autumn.api.sign.sdk.http;

import com.autumn.api.sign.sdk.ClientConfiguration;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;

import java.util.concurrent.TimeUnit;

class ConnectionManagerFactory {
  ConnectionManagerFactory() {}

  public static PoolingClientConnectionManager createPoolingClientConnManager(
      ClientConfiguration config) {
    PoolingClientConnectionManager connectionManager =
        new PoolingClientConnectionManager(
            SchemeRegistryFactory.createDefault(),
            config.getConnectionTTL(),
            TimeUnit.MILLISECONDS);
    connectionManager.setDefaultMaxPerRoute(config.getMaxConnections());
    connectionManager.setMaxTotal(config.getMaxConnections());
    return connectionManager;
  }
}
