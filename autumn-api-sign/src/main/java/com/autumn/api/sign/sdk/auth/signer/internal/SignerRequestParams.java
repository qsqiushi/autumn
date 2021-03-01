//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autumn.api.sign.sdk.auth.signer.internal;

import com.autumn.api.sign.sdk.Request;

public final class SignerRequestParams {
  private final Request<?> request;
  private final long signingDateTimeMilli;
  private final String formattedSigningDateTime;
  private final String formattedSigningDate;
  private final String signingAlgorithm;

  public SignerRequestParams(Request<?> request, String signingAlgorithm) {
    this(request, signingAlgorithm, (String) null);
  }

  public SignerRequestParams(Request<?> request, String signingAlgorithm, String signDate) {
    if (request == null) {
      throw new IllegalArgumentException("Request cannot be null");
    } else if (signingAlgorithm == null) {
      throw new IllegalArgumentException("Signing Algorithm cannot be null");
    } else {
      if (null == signDate) {
        this.signingDateTimeMilli = this.getSigningDate(request);
      } else {
        this.signingDateTimeMilli = this.getSigningDate(signDate);
      }

      this.request = request;
      this.formattedSigningDate = SignerUtils.formatDateStamp(this.signingDateTimeMilli);
      this.formattedSigningDateTime = SignerUtils.formatTimestamp(this.signingDateTimeMilli);
      this.signingAlgorithm = signingAlgorithm;
    }
  }

  private final long getSigningDate(Request<?> request) {
    return System.currentTimeMillis() - (long) (request.getTimeOffset() * 1000);
  }

  private final long getSigningDate(String signDate) {
    return SignerUtils.parseMillis(signDate);
  }

  public Request<?> getRequest() {
    return this.request;
  }

  public String getFormattedSigningDateTime() {
    return this.formattedSigningDateTime;
  }

  public long getSigningDateTimeMilli() {
    return this.signingDateTimeMilli;
  }

  public String getFormattedSigningDate() {
    return this.formattedSigningDate;
  }

  public String getSigningAlgorithm() {
    return this.signingAlgorithm;
  }
}
