//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autumn.api.sign.sdk.http;

import com.autumn.api.sign.sdk.WebServiceClient;
import com.autumn.api.sign.sdk.annotation.NotThreadSafe;
import com.autumn.api.sign.sdk.auth.credentials.Credentials;
import com.autumn.api.sign.sdk.auth.signer.Signer;

import java.util.List;

@NotThreadSafe
public class ExecutionContext {
  private final List<RequestHandler> requestHandlers;
  private final WebServiceClient client;
  private String contextUserAgent;
  private Credentials credentials;

  public ExecutionContext(List<RequestHandler> requestHandlers, WebServiceClient client) {
    this.requestHandlers = requestHandlers;
    this.client = client;
  }

  public String getContextUserAgent() {
    return this.contextUserAgent;
  }

  public void setContextUserAgent(String contextUserAgent) {
    this.contextUserAgent = contextUserAgent;
  }

  public List<RequestHandler> getRequestHandlers() {
    return this.requestHandlers;
  }

  protected WebServiceClient getClient() {
    return this.client;
  }

  public Signer getSigner() {
    return this.client == null ? null : this.client.getSigner();
  }

  public Credentials getCredentials() {
    return this.credentials;
  }

  public void setCredentials(Credentials credentials) {
    this.credentials = credentials;
  }
}
