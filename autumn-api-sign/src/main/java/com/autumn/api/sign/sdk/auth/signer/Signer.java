package com.autumn.api.sign.sdk.auth.signer;

import com.autumn.api.sign.sdk.Request;
import com.autumn.api.sign.sdk.auth.credentials.Credentials;

public abstract interface Signer {
  public abstract void sign(Request<?> paramRequest, Credentials paramCredentials);
}
