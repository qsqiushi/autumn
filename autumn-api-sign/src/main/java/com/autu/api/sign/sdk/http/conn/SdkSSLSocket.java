
package com.autu.api.sign.sdk.http.conn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

public class SdkSSLSocket extends SSLSocket {
    private static final Log log = LogFactory.getLog(SdkSSLSocket.class);
    private final SSLSocket sock;

    public SdkSSLSocket(SSLSocket sock) {
        this.sock = sock;
        if (log.isDebugEnabled()) {
            log.debug("created: " + this.endpoint());
        }

    }

    private String endpoint() {
        return this.sock.getInetAddress() + ":" + this.sock.getPort();
    }

    public void connect(SocketAddress endpoint) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("connecting to: " + endpoint);
        }

        this.sock.connect(endpoint);
        if (log.isDebugEnabled()) {
            log.debug("connected to: " + this.endpoint());
        }

    }

    public void connect(SocketAddress endpoint, int timeout) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("connecting to: " + endpoint);
        }

        this.sock.connect(endpoint, timeout);
        if (log.isDebugEnabled()) {
            log.debug("connected to: " + this.endpoint());
        }

    }

    public void bind(SocketAddress bindpoint) throws IOException {
        this.sock.bind(bindpoint);
    }

    public InetAddress getInetAddress() {
        return this.sock.getInetAddress();
    }

    public InetAddress getLocalAddress() {
        return this.sock.getLocalAddress();
    }

    public int getPort() {
        return this.sock.getPort();
    }

    public int getLocalPort() {
        return this.sock.getLocalPort();
    }

    public SocketAddress getRemoteSocketAddress() {
        return this.sock.getRemoteSocketAddress();
    }

    public SocketAddress getLocalSocketAddress() {
        return this.sock.getLocalSocketAddress();
    }

    public SocketChannel getChannel() {
        return this.sock.getChannel();
    }

    public InputStream getInputStream() throws IOException {
        return this.sock.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return this.sock.getOutputStream();
    }

    public void setTcpNoDelay(boolean on) throws SocketException {
        this.sock.setTcpNoDelay(on);
    }

    public boolean getTcpNoDelay() throws SocketException {
        return this.sock.getTcpNoDelay();
    }

    public void setSoLinger(boolean on, int linger) throws SocketException {
        this.sock.setSoLinger(on, linger);
    }

    public int getSoLinger() throws SocketException {
        return this.sock.getSoLinger();
    }

    public void sendUrgentData(int data) throws IOException {
        this.sock.sendUrgentData(data);
    }

    public void setOOBInline(boolean on) throws SocketException {
        this.sock.setOOBInline(on);
    }

    public boolean getOOBInline() throws SocketException {
        return this.sock.getOOBInline();
    }

    public void setSoTimeout(int timeout) throws SocketException {
        this.sock.setSoTimeout(timeout);
    }

    public int getSoTimeout() throws SocketException {
        return this.sock.getSoTimeout();
    }

    public void setSendBufferSize(int size) throws SocketException {
        this.sock.setSendBufferSize(size);
    }

    public int getSendBufferSize() throws SocketException {
        return this.sock.getSendBufferSize();
    }

    public void setReceiveBufferSize(int size) throws SocketException {
        this.sock.setReceiveBufferSize(size);
    }

    public int getReceiveBufferSize() throws SocketException {
        return this.sock.getReceiveBufferSize();
    }

    public void setKeepAlive(boolean on) throws SocketException {
        this.sock.setKeepAlive(on);
    }

    public boolean getKeepAlive() throws SocketException {
        return this.sock.getKeepAlive();
    }

    public void setTrafficClass(int tc) throws SocketException {
        this.sock.setTrafficClass(tc);
    }

    public int getTrafficClass() throws SocketException {
        return this.sock.getTrafficClass();
    }

    public void setReuseAddress(boolean on) throws SocketException {
        this.sock.setReuseAddress(on);
    }

    public boolean getReuseAddress() throws SocketException {
        return this.sock.getReuseAddress();
    }

    public void close() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("closing " + this.endpoint());
        }

        this.sock.close();
    }

    public void shutdownInput() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("shutting down input of " + this.endpoint());
        }

        this.sock.shutdownInput();
    }

    public void shutdownOutput() throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("shutting down output of " + this.endpoint());
        }

        this.sock.shutdownOutput();
    }

    public String toString() {
        return this.sock.toString();
    }

    public boolean isConnected() {
        return this.sock.isConnected();
    }

    public boolean isBound() {
        return this.sock.isBound();
    }

    public boolean isClosed() {
        return this.sock.isClosed();
    }

    public boolean isInputShutdown() {
        return this.sock.isInputShutdown();
    }

    public boolean isOutputShutdown() {
        return this.sock.isOutputShutdown();
    }

    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
        this.sock.setPerformancePreferences(connectionTime, latency, bandwidth);
    }

    public String[] getSupportedCipherSuites() {
        return this.sock.getSupportedCipherSuites();
    }

    public String[] getEnabledCipherSuites() {
        return this.sock.getEnabledCipherSuites();
    }

    public void setEnabledCipherSuites(String[] suites) {
        this.sock.setEnabledCipherSuites(suites);
    }

    public String[] getSupportedProtocols() {
        return this.sock.getSupportedProtocols();
    }

    public String[] getEnabledProtocols() {
        return this.sock.getEnabledProtocols();
    }

    public void setEnabledProtocols(String[] protocols) {
        this.sock.setEnabledProtocols(protocols);
    }

    public SSLSession getSession() {
        return this.sock.getSession();
    }

    public void addHandshakeCompletedListener(HandshakeCompletedListener listener) {
        this.sock.addHandshakeCompletedListener(listener);
    }

    public void removeHandshakeCompletedListener(HandshakeCompletedListener listener) {
        this.sock.removeHandshakeCompletedListener(listener);
    }

    public void startHandshake() throws IOException {
        this.sock.startHandshake();
    }

    public void setUseClientMode(boolean mode) {
        this.sock.setUseClientMode(mode);
    }

    public boolean getUseClientMode() {
        return this.sock.getUseClientMode();
    }

    public void setNeedClientAuth(boolean need) {
        this.sock.setNeedClientAuth(need);
    }

    public boolean getNeedClientAuth() {
        return this.sock.getNeedClientAuth();
    }

    public void setWantClientAuth(boolean want) {
        this.sock.setWantClientAuth(want);
    }

    public boolean getWantClientAuth() {
        return this.sock.getWantClientAuth();
    }

    public void setEnableSessionCreation(boolean flag) {
        this.sock.setEnableSessionCreation(flag);
    }

    public boolean getEnableSessionCreation() {
        return this.sock.getEnableSessionCreation();
    }
}
