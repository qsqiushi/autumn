package com.autumn.api.sign.sdk.auth.signer;

import com.autumn.api.sign.sdk.Request;
import com.autumn.api.sign.sdk.auth.credentials.Credentials;

import java.util.Date;

public abstract interface Presigner {
  public abstract void presignRequest(
      Request<?> paramRequest, Credentials paramCredentials, Date paramDate);
}
