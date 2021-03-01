//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autumn.api.sign.sdk;

import com.autumn.api.sign.sdk.annotation.NotThreadSafe;
import org.apache.http.conn.ssl.SSLSocketFactory;

@NotThreadSafe
public final class ApacheHttpClientConfig {
    private SSLSocketFactory sslSocketFactory;

    ApacheHttpClientConfig() {}

    ApacheHttpClientConfig(ApacheHttpClientConfig that) {
        this.sslSocketFactory = that.sslSocketFactory;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return this.sslSocketFactory;
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public ApacheHttpClientConfig withSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }
}
