package com.autu.api.sign.sdk.auth.signer;

import com.autu.api.sign.sdk.auth.credentials.Credentials;
import com.autu.api.sign.sdk.Request;

public abstract interface VerifySigner extends Signer {
    public abstract boolean verify(Request<?> paramRequest, Credentials paramCredentials);
}
