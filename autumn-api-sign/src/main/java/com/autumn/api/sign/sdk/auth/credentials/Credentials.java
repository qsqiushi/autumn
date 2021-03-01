package com.autumn.api.sign.sdk.auth.credentials;

public abstract interface Credentials {
    public abstract String getAccessKeyId();

    public abstract String getSecretKey();
}
