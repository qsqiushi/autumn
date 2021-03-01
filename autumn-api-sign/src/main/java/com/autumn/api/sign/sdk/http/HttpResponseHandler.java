package com.autumn.api.sign.sdk.http;

public interface HttpResponseHandler<T> {
    T handle(HttpResponse var1) throws Exception;
}
