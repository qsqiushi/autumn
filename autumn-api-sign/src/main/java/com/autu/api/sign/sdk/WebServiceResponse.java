
package com.autu.api.sign.sdk;

public class WebServiceResponse<T> {
    private T result;

    public WebServiceResponse() {}

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
