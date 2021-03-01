//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autumn.api.sign.sdk;

import com.autumn.api.sign.sdk.auth.credentials.Credentials;
import com.autumn.api.sign.sdk.annotation.NotThreadSafe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@NotThreadSafe
public abstract class WebServiceRequest implements Cloneable {
    public static final WebServiceRequest NOOP = new WebServiceRequest() {};
    private final RequestClientOptions requestClientOptions = new RequestClientOptions();
    private Credentials credentials;
    private Map<String, String> customRequestHeaders;

    public WebServiceRequest() {}

    public void setRequestCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Credentials getRequestCredentials() {
        return this.credentials;
    }

    public Map<String, String> copyPrivateRequestParameters() {
        return new HashMap();
    }

    public RequestClientOptions getRequestClientOptions() {
        return this.requestClientOptions;
    }

    public Map<String, String> getCustomRequestHeaders() {
        return this.customRequestHeaders == null ? null : Collections.unmodifiableMap(this.customRequestHeaders);
    }

    public String putCustomRequestHeader(String name, String value) {
        if (this.customRequestHeaders == null) {
            this.customRequestHeaders = new HashMap();
        }

        return (String)this.customRequestHeaders.put(name, value);
    }

    public final int getReadLimit() {
        return this.requestClientOptions.getReadLimit();
    }

    protected final <T extends WebServiceRequest> T copyBaseTo(T target) {
        if (this.customRequestHeaders != null) {
            Iterator var2 = this.customRequestHeaders.entrySet().iterator();

            while (var2.hasNext()) {
                Entry<String, String> e = (Entry)var2.next();
                target.putCustomRequestHeader((String)e.getKey(), (String)e.getValue());
            }
        }

        target.setRequestCredentials(this.credentials);
        this.requestClientOptions.copyTo(target.getRequestClientOptions());
        return target;
    }

    @Override
    public WebServiceRequest clone() {
        try {
            return (WebServiceRequest)super.clone();
        } catch (CloneNotSupportedException var2) {
            throw new IllegalStateException(
                "Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", var2);
        }
    }
}
