package com.autu.api.sign.sdk.http;

public interface HttpResponseHandler<T> {
    T handle(HttpResponse var1) throws Exception;
}
