//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autumn.api.sign.sdk;

public class ClientException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ClientException(String message, Throwable t) {
        super(message, t);
    }

    public ClientException(String message) {
        super(message);
    }

    public ClientException(Throwable t) {
        super(t);
    }

    public boolean isRetryable() {
        return true;
    }
}
