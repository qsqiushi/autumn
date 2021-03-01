
package com.autumn.api.sign.sdk.http;

public enum HttpMethodName {

    GET(1), POST(2), PUT(3), PATCH(4), DELETE(5), HEAD(6), OPTIONS(7);

    private final int code;

    private HttpMethodName(int code) {
        this.code = code;
    }
}
