package com.autumn.api.sign.sdk.auth.signer;

import com.autumn.api.sign.sdk.auth.credentials.Credentials;
import com.autumn.api.sign.sdk.Request;

public abstract interface VerifySigner extends Signer {
    public abstract boolean verify(Request<?> paramRequest, Credentials paramCredentials);
}
