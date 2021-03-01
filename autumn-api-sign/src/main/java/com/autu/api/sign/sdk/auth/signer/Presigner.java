package com.autu.api.sign.sdk.auth.signer;

import com.autu.api.sign.sdk.Request;
import com.autu.api.sign.sdk.auth.credentials.Credentials;

import java.util.Date;

public abstract interface Presigner {
    public abstract void presignRequest(Request<?> paramRequest, Credentials paramCredentials, Date paramDate);
}
