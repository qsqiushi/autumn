
package com.autu.api.sign.sdk.http;

import com.autu.api.sign.sdk.ClientConfiguration;
import com.autu.api.sign.sdk.ClientException;
import com.autu.api.sign.sdk.http.conn.ssl.TLSSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class HttpClientFactory {
    private static final Log log = LogFactory.getLog(HttpClientFactory.class);

    HttpClientFactory() {}

    public HttpClient createHttpClient(ClientConfiguration config) {
        HttpParams httpClientParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpClientParams, config.getConnectionTimeout());
        HttpConnectionParams.setSoTimeout(httpClientParams, config.getSocketTimeout());
        HttpConnectionParams.setStaleCheckingEnabled(httpClientParams, true);
        HttpConnectionParams.setTcpNoDelay(httpClientParams, true);
        HttpConnectionParams.setSoKeepalive(httpClientParams, config.useTcpKeepAlive());
        int socketSendBufferSizeHint = config.getSocketBufferSizeHints()[0];
        int socketReceiveBufferSizeHint = config.getSocketBufferSizeHints()[1];
        if (socketSendBufferSizeHint > 0 || socketReceiveBufferSizeHint > 0) {
            HttpConnectionParams.setSocketBufferSize(httpClientParams,
                Math.max(socketSendBufferSizeHint, socketReceiveBufferSizeHint));
        }

        PoolingClientConnectionManager connectionManager =
            ConnectionManagerFactory.createPoolingClientConnManager(config);
        DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager, httpClientParams);
        httpClient.setRedirectStrategy(new NeverFollowRedirectStrategy());
        if (config.getLocalAddress() != null) {
            ConnRouteParams.setLocalAddress(httpClientParams, config.getLocalAddress());
        }

        try {
            Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
            SSLSocketFactory sf = config.getApacheHttpClientConfig().getSslSocketFactory();
            if (sf == null) {
                sf = new TLSSocketFactory(SSLContext.getDefault(), SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            }

            Scheme https = new Scheme("https", 443, (SchemeSocketFactory)sf);
            SchemeRegistry sr = connectionManager.getSchemeRegistry();
            sr.register(http);
            sr.register(https);
        } catch (NoSuchAlgorithmException var14) {
            throw new ClientException("Unable to access default SSL context", var14);
        }

        String proxyHost = config.getProxyHost();
        int proxyPort = config.getProxyPort();
        if (proxyHost != null && proxyPort > 0) {
            log.info("Configuring Proxy. Proxy Host: " + proxyHost + " Proxy Port: " + proxyPort);
            HttpHost proxyHttpHost = new HttpHost(proxyHost, proxyPort);
            httpClient.getParams().setParameter("http.route.default-proxy", proxyHttpHost);
            String proxyUsername = config.getProxyUsername();
            String proxyPassword = config.getProxyPassword();
            String proxyDomain = config.getProxyDomain();
            String proxyWorkstation = config.getProxyWorkstation();
            if (proxyUsername != null && proxyPassword != null) {
                httpClient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort),
                    new NTCredentials(proxyUsername, proxyPassword, proxyWorkstation, proxyDomain));
            }

            if (config.isPreemptiveBasicProxyAuth()) {
                httpClient.addRequestInterceptor(
                    new HttpClientFactory.PreemptiveProxyAuth(proxyHttpHost), 0);
            }
        }

        if (config.useGzip()) {
            httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
                public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
                    if (!request.containsHeader("Accept-Encoding")) {
                        request.addHeader("Accept-Encoding", "gzip");
                    }

                }

            });
            httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
                public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        Header ceheader = entity.getContentEncoding();
                        if (ceheader != null) {
                            HeaderElement[] codecs = ceheader.getElements();
                            for (int i = 0; i < codecs.length; i++) {
                                if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                                    response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                                    return;
                                }
                            }
                        }
                    }
                }
            });
        }
        return httpClient;
    }

    private static final class NeverFollowRedirectStrategy implements RedirectStrategy {

        @Override
        public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
            throws ProtocolException {
            return false;
        }

        @Override
        public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
            throws ProtocolException {
            return null;
        }
    }

    private static class PreemptiveProxyAuth implements HttpRequestInterceptor {
        private final HttpHost proxyHost;

        public PreemptiveProxyAuth(HttpHost proxyHost) {
            this.proxyHost = proxyHost;
        }

        public void process(HttpRequest request, HttpContext context) {
            BasicScheme basicScheme = new BasicScheme(ChallengeState.PROXY);

            if (context.getAttribute("http.auth.auth-cache") == null) {
                AuthCache authCache = new BasicAuthCache();
                authCache.put(this.proxyHost, basicScheme);
                context.setAttribute("http.auth.auth-cache", authCache);
            } else {
                AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
                authCache.put(this.proxyHost, basicScheme);
            }
        }
    }
}
