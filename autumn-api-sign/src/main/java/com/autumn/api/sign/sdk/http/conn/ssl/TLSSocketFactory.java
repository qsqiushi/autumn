package com.autumn.api.sign.sdk.http.conn.ssl;

import com.autumn.api.sign.sdk.annotation.ThreadSafe;
import com.autumn.api.sign.sdk.http.conn.SdkSSLSocket;
import com.autumn.api.sign.sdk.http.conn.SdkSocket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.params.HttpParams;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ThreadSafe
public class TLSSocketFactory extends SSLSocketFactory {
  private static final Log log = LogFactory.getLog(TLSSocketFactory.class);

  public TLSSocketFactory(SSLContext sslContext, X509HostnameVerifier hostnameVerifier) {
    super(sslContext, hostnameVerifier);
  }

  protected final void prepareSocket(SSLSocket socket) {
    String[] supported = socket.getSupportedProtocols();
    String[] enabled = socket.getEnabledProtocols();
    if (log.isDebugEnabled()) {
      log.debug(
          "socket.getSupportedProtocols(): "
              + Arrays.toString(supported)
              + ", socket.getEnabledProtocols(): "
              + Arrays.toString(enabled));
    }

    List<String> target = new ArrayList();
    int var7;
    if (supported != null) {
      TLSProtocol[] values = TLSProtocol.values();
      TLSProtocol[] var6 = values;
      var7 = values.length;

      for (int var8 = 0; var8 < var7; ++var8) {
        TLSProtocol tslprotocol = var6[var8];
        String pname = tslprotocol.getProtocolName();
        if (this.existsIn(pname, supported)) {
          target.add(pname);
        }
      }
    }

    String[] enabling;
    if (enabled != null) {
      enabling = enabled;
      int var12 = enabled.length;

      for (var7 = 0; var7 < var12; ++var7) {
        String pname = enabling[var7];
        if (!target.contains(pname)) {
          target.add(pname);
        }
      }
    }

    if (target.size() > 0) {
      enabling = (String[]) target.toArray(new String[target.size()]);
      socket.setEnabledProtocols(enabling);
      if (log.isDebugEnabled()) {
        log.debug("TLS protocol enabled for SSL handshake: " + Arrays.toString(enabling));
      }
    }
  }

  private boolean existsIn(String element, String[] a) {
    String[] var3 = a;
    int var4 = a.length;

    for (int var5 = 0; var5 < var4; ++var5) {
      String s = var3[var5];
      if (element.equals(s)) {
        return true;
      }
    }

    return false;
  }

  public Socket connectSocket(
      Socket socket,
      InetSocketAddress remoteAddress,
      InetSocketAddress localAddress,
      HttpParams params)
      throws IOException, ConnectTimeoutException {
    if (log.isDebugEnabled()) {
      log.debug("connecting to " + remoteAddress.getAddress() + ":" + remoteAddress.getPort());
    }

    this.verifyMasterSecret(super.connectSocket(socket, remoteAddress, localAddress, params));
    return (Socket)
        (socket instanceof SSLSocket
            ? new SdkSSLSocket((SSLSocket) socket)
            : new SdkSocket(socket));
  }

  private void verifyMasterSecret(Socket sock) {
    if (sock instanceof SSLSocket) {
      SSLSocket ssl = (SSLSocket) sock;
      SSLSession session = ssl.getSession();
      if (session != null) {
        String className = session.getClass().getName();
        if ("sun.security.ssl.SSLSessionImpl".equals(className)) {
          try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod("getMasterSecret");
            method.setAccessible(true);
            Object masterSecret = method.invoke(session);
            if (masterSecret == null) {
              throw (SecurityException)
                  this.log(new SecurityException("Invalid SSL master secret"));
            }
          } catch (ClassNotFoundException var8) {
            this.failedToVerifyMasterSecret(var8);
          } catch (NoSuchMethodException var9) {
            this.failedToVerifyMasterSecret(var9);
          } catch (IllegalAccessException var10) {
            this.failedToVerifyMasterSecret(var10);
          } catch (InvocationTargetException var11) {
            this.failedToVerifyMasterSecret(var11.getCause());
          }
        }
      }
    }
  }

  private void failedToVerifyMasterSecret(Throwable t) {
    if (log.isDebugEnabled()) {
      log.debug("Failed to verify the SSL master secret", t);
    }
  }

  private <T extends Throwable> T log(T t) {
    if (log.isDebugEnabled()) {
      log.debug("", t);
    }

    return t;
  }
}
