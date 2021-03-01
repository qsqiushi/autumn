//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autumn.api.sign.sdk;

import com.autumn.api.sign.sdk.annotation.NotThreadSafe;

import java.net.InetAddress;

@NotThreadSafe
public class ClientConfiguration {
  public static final int DEFAULT_CONNECTION_TIMEOUT = 50000;
  public static final int DEFAULT_SOCKET_TIMEOUT = 50000;
  public static final int DEFAULT_MAX_CONNECTIONS = 50;
  public static final String DEFAULT_USER_AGENT = "Apache HTTPClient";
  public static final boolean DEFAULT_USE_GZIP = false;
  public static final long DEFAULT_CONNECTION_TTL = -1L;
  public static final boolean DEFAULT_TCP_KEEP_ALIVE = false;
  private final ApacheHttpClientConfig apacheHttpClientConfig;
  private String userAgent = "Apache HTTPClient";
  private InetAddress localAddress;
  private Protocol protocol;
  private String proxyHost;
  private int proxyPort;
  private String proxyUsername;
  private String proxyPassword;
  private String proxyDomain;
  private String proxyWorkstation;
  private boolean preemptiveBasicProxyAuth;
  private int maxConnections;
  private int socketTimeout;
  private int connectionTimeout;
  private int socketSendBufferSizeHint;
  private int socketReceiveBufferSizeHint;
  private boolean useGzip;
  private String signerOverride;
  private long connectionTTL;
  private boolean tcpKeepAlive;

  public ClientConfiguration() {
    this.protocol = Protocol.HTTPS;
    this.proxyHost = null;
    this.proxyPort = -1;
    this.proxyUsername = null;
    this.proxyPassword = null;
    this.proxyDomain = null;
    this.proxyWorkstation = null;
    this.maxConnections = 50;
    this.socketTimeout = 50000;
    this.connectionTimeout = 50000;
    this.socketSendBufferSizeHint = 0;
    this.socketReceiveBufferSizeHint = 0;
    this.useGzip = false;
    this.connectionTTL = -1L;
    this.tcpKeepAlive = false;
    this.apacheHttpClientConfig = new ApacheHttpClientConfig();
  }

  public ClientConfiguration(ClientConfiguration other) {
    this.protocol = Protocol.HTTPS;
    this.proxyHost = null;
    this.proxyPort = -1;
    this.proxyUsername = null;
    this.proxyPassword = null;
    this.proxyDomain = null;
    this.proxyWorkstation = null;
    this.maxConnections = 50;
    this.socketTimeout = 50000;
    this.connectionTimeout = 50000;
    this.socketSendBufferSizeHint = 0;
    this.socketReceiveBufferSizeHint = 0;
    this.useGzip = false;
    this.connectionTTL = -1L;
    this.tcpKeepAlive = false;
    this.connectionTimeout = other.connectionTimeout;
    this.maxConnections = other.maxConnections;
    this.localAddress = other.localAddress;
    this.protocol = other.protocol;
    this.proxyDomain = other.proxyDomain;
    this.proxyHost = other.proxyHost;
    this.proxyPassword = other.proxyPassword;
    this.proxyPort = other.proxyPort;
    this.proxyUsername = other.proxyUsername;
    this.proxyWorkstation = other.proxyWorkstation;
    this.preemptiveBasicProxyAuth = other.preemptiveBasicProxyAuth;
    this.socketTimeout = other.socketTimeout;
    this.userAgent = other.userAgent;
    this.useGzip = other.useGzip;
    this.socketReceiveBufferSizeHint = other.socketReceiveBufferSizeHint;
    this.socketSendBufferSizeHint = other.socketSendBufferSizeHint;
    this.signerOverride = other.signerOverride;
    this.apacheHttpClientConfig = new ApacheHttpClientConfig(other.apacheHttpClientConfig);
  }

  public Protocol getProtocol() {
    return this.protocol;
  }

  public void setProtocol(Protocol protocol) {
    this.protocol = protocol;
  }

  public ClientConfiguration withProtocol(Protocol protocol) {
    this.setProtocol(protocol);
    return this;
  }

  public int getMaxConnections() {
    return this.maxConnections;
  }

  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  public ClientConfiguration withMaxConnections(int maxConnections) {
    this.setMaxConnections(maxConnections);
    return this;
  }

  public String getUserAgent() {
    return this.userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public ClientConfiguration withUserAgent(String userAgent) {
    this.setUserAgent(userAgent);
    return this;
  }

  public InetAddress getLocalAddress() {
    return this.localAddress;
  }

  public void setLocalAddress(InetAddress localAddress) {
    this.localAddress = localAddress;
  }

  public ClientConfiguration withLocalAddress(InetAddress localAddress) {
    this.setLocalAddress(localAddress);
    return this;
  }

  public String getProxyHost() {
    return this.proxyHost;
  }

  public void setProxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
  }

  public ClientConfiguration withProxyHost(String proxyHost) {
    this.setProxyHost(proxyHost);
    return this;
  }

  public int getProxyPort() {
    return this.proxyPort;
  }

  public void setProxyPort(int proxyPort) {
    this.proxyPort = proxyPort;
  }

  public ClientConfiguration withProxyPort(int proxyPort) {
    this.setProxyPort(proxyPort);
    return this;
  }

  public String getProxyUsername() {
    return this.proxyUsername;
  }

  public void setProxyUsername(String proxyUsername) {
    this.proxyUsername = proxyUsername;
  }

  public ClientConfiguration withProxyUsername(String proxyUsername) {
    this.setProxyUsername(proxyUsername);
    return this;
  }

  public String getProxyPassword() {
    return this.proxyPassword;
  }

  public void setProxyPassword(String proxyPassword) {
    this.proxyPassword = proxyPassword;
  }

  public ClientConfiguration withProxyPassword(String proxyPassword) {
    this.setProxyPassword(proxyPassword);
    return this;
  }

  public String getProxyDomain() {
    return this.proxyDomain;
  }

  public void setProxyDomain(String proxyDomain) {
    this.proxyDomain = proxyDomain;
  }

  public ClientConfiguration withProxyDomain(String proxyDomain) {
    this.setProxyDomain(proxyDomain);
    return this;
  }

  public String getProxyWorkstation() {
    return this.proxyWorkstation;
  }

  public void setProxyWorkstation(String proxyWorkstation) {
    this.proxyWorkstation = proxyWorkstation;
  }

  public ClientConfiguration withProxyWorkstation(String proxyWorkstation) {
    this.setProxyWorkstation(proxyWorkstation);
    return this;
  }

  public int getSocketTimeout() {
    return this.socketTimeout;
  }

  public void setSocketTimeout(int socketTimeout) {
    this.socketTimeout = socketTimeout;
  }

  public ClientConfiguration withSocketTimeout(int socketTimeout) {
    this.setSocketTimeout(socketTimeout);
    return this;
  }

  public int getConnectionTimeout() {
    return this.connectionTimeout;
  }

  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public ClientConfiguration withConnectionTimeout(int connectionTimeout) {
    this.setConnectionTimeout(connectionTimeout);
    return this;
  }

  public boolean useGzip() {
    return this.useGzip;
  }

  public void setUseGzip(boolean use) {
    this.useGzip = use;
  }

  public ClientConfiguration withGzip(boolean use) {
    this.setUseGzip(use);
    return this;
  }

  public int[] getSocketBufferSizeHints() {
    return new int[] {this.socketSendBufferSizeHint, this.socketReceiveBufferSizeHint};
  }

  public void setSocketBufferSizeHints(
      int socketSendBufferSizeHint, int socketReceiveBufferSizeHint) {
    this.socketSendBufferSizeHint = socketSendBufferSizeHint;
    this.socketReceiveBufferSizeHint = socketReceiveBufferSizeHint;
  }

  public ClientConfiguration withSocketBufferSizeHints(
      int socketSendBufferSizeHint, int socketReceiveBufferSizeHint) {
    this.setSocketBufferSizeHints(socketSendBufferSizeHint, socketReceiveBufferSizeHint);
    return this;
  }

  public String getSignerOverride() {
    return this.signerOverride;
  }

  public void setSignerOverride(String value) {
    this.signerOverride = value;
  }

  public ClientConfiguration withSignerOverride(String value) {
    this.setSignerOverride(value);
    return this;
  }

  public boolean isPreemptiveBasicProxyAuth() {
    return this.preemptiveBasicProxyAuth;
  }

  public void setPreemptiveBasicProxyAuth(Boolean preemptiveBasicProxyAuth) {
    this.preemptiveBasicProxyAuth = preemptiveBasicProxyAuth;
  }

  public ClientConfiguration withPreemptiveBasicProxyAuth(boolean preemptiveBasicProxyAuth) {
    this.setPreemptiveBasicProxyAuth(preemptiveBasicProxyAuth);
    return this;
  }

  public long getConnectionTTL() {
    return this.connectionTTL;
  }

  public void setConnectionTTL(long connectionTTL) {
    this.connectionTTL = connectionTTL;
  }

  public ClientConfiguration withConnectionTTL(long connectionTTL) {
    this.setConnectionTTL(connectionTTL);
    return this;
  }

  public boolean useTcpKeepAlive() {
    return this.tcpKeepAlive;
  }

  public void setUseTcpKeepAlive(boolean use) {
    this.tcpKeepAlive = use;
  }

  public ClientConfiguration withTcpKeepAlive(boolean use) {
    this.setUseTcpKeepAlive(use);
    return this;
  }

  public ApacheHttpClientConfig getApacheHttpClientConfig() {
    return this.apacheHttpClientConfig;
  }
}
