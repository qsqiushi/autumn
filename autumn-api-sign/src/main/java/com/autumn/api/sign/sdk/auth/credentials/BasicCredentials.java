package com.autumn.api.sign.sdk.auth.credentials;

public class BasicCredentials implements Credentials {
  private final String accessKey;
  private final String secretKey;

  public BasicCredentials(String accessKey, String secretKey) {
    if (accessKey == null) {
      throw new IllegalArgumentException("Access key cannot be null.");
    } else if (secretKey == null) {
      throw new IllegalArgumentException("Secret key cannot be null.");
    } else {
      this.accessKey = accessKey;
      this.secretKey = secretKey;
    }
  }

  @Override
  public String getAccessKeyId() {
    return this.accessKey;
  }

  @Override
  public String getSecretKey() {
    return this.secretKey;
  }
}
