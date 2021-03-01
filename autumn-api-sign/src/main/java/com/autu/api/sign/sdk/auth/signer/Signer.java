package com.autu.api.sign.sdk.auth.signer;

import com.autu.api.sign.sdk.auth.credentials.Credentials;
import com.autu.api.sign.sdk.Request;

public abstract interface Signer {
    public abstract void sign(Request<?> paramRequest, Credentials paramCredentials);
}
