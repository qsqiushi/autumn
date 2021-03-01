//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autumn.api.sign.sdk;

import com.autumn.api.sign.sdk.http.HttpResponse;

public final class Response<T> {
    private final T response;
    private final HttpResponse httpResponse;

    public Response(T response, HttpResponse httpResponse) {
        this.response = response;
        this.httpResponse = httpResponse;
    }

    public T getResponse() {
        return this.response;
    }

    public HttpResponse getHttpResponse() {
        return this.httpResponse;
    }
}
