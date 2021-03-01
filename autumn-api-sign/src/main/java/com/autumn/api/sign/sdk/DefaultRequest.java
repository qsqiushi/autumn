
package com.autumn.api.sign.sdk;

import com.autumn.api.sign.sdk.annotation.NotThreadSafe;
import com.autumn.api.sign.sdk.http.HttpMethodName;

import java.io.InputStream;
import java.net.URI;
import java.util.*;

@NotThreadSafe
public class DefaultRequest<T> implements Request<T> {
    private String resourcePath;
    private Map<String, List<String>> parameters;
    private Map<String, String> headers;
    private URI endpoint;
    private final WebServiceRequest originalRequest;
    private HttpMethodName httpMethod;
    private InputStream content;
    private int timeOffset;

    public DefaultRequest(WebServiceRequest originalRequest) {
        this.parameters = new LinkedHashMap();
        this.headers = new HashMap();
        this.httpMethod = HttpMethodName.POST;
        this.originalRequest = originalRequest == null ? WebServiceRequest.NOOP : originalRequest;
    }

    public DefaultRequest() {
        this((WebServiceRequest)null);
    }

    public WebServiceRequest getOriginalRequest() {
        return this.originalRequest;
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public void addParameter(String name, String value) {
        List<String> paramList = (List)this.parameters.get(name);
        if (paramList == null) {
            paramList = new ArrayList();
            this.parameters.put(name, paramList);
        }

        ((List)paramList).add(value);
    }

    public void addParameters(String name, List<String> values) {
        if (values != null) {
            Iterator var3 = values.iterator();

            while (var3.hasNext()) {
                String value = (String)var3.next();
                this.addParameter(name, value);
            }

        }
    }

    public Map<String, List<String>> getParameters() {
        return this.parameters;
    }

    public Request<T> withParameter(String name, String value) {
        this.addParameter(name, value);
        return this;
    }

    public HttpMethodName getHttpMethod() {
        return this.httpMethod;
    }

    public void setHttpMethod(HttpMethodName httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setEndpoint(URI endpoint) {
        this.endpoint = endpoint;
    }

    public URI getEndpoint() {
        return this.endpoint;
    }

    public InputStream getContent() {
        return this.content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    public void setParameters(Map<String, List<String>> parameters) {
        this.parameters.clear();
        this.parameters.putAll(parameters);
    }

    public int getTimeOffset() {
        return this.timeOffset;
    }

    public void setTimeOffset(int timeOffset) {
        this.timeOffset = timeOffset;
    }

    public Request<T> withTimeOffset(int timeOffset) {
        this.setTimeOffset(timeOffset);
        return this;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getHttpMethod()).append(" ");
        builder.append(this.getEndpoint()).append(" ");
        String resourcePath = this.getResourcePath();
        if (resourcePath == null) {
            builder.append("/");
        } else {
            if (!resourcePath.startsWith("/")) {
                builder.append("/");
            }

            builder.append(resourcePath);
        }

        builder.append(" ");
        Iterator var3;
        String key;
        if (!this.getParameters().isEmpty()) {
            builder.append("Parameters: (");
            var3 = this.getParameters().keySet().iterator();

            while (var3.hasNext()) {
                key = (String)var3.next();
                List<String> value = (List)this.getParameters().get(key);
                builder.append(key).append(": ").append(String.join(";", value)).append(", ");
            }

            builder.append(") ");
        }

        if (!this.getHeaders().isEmpty()) {
            builder.append("Headers: (");
            var3 = this.getHeaders().keySet().iterator();

            while (var3.hasNext()) {
                key = (String)var3.next();
                String value = (String)this.getHeaders().get(key);
                builder.append(key).append(": ").append(value).append(", ");
            }

            builder.append(") ");
        }

        return builder.toString();
    }
}
