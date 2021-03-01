//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autumn.api.sign.sdk.http;

import com.autumn.api.sign.sdk.Request;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
  private final Request<?> request;
  private final HttpRequestBase httpRequest;
  private String statusText;
  private int statusCode;
  private InputStream content;
  private Map<String, String> headers = new HashMap();

  public HttpResponse(Request<?> request, HttpRequestBase httpRequest) {
    this.request = request;
    this.httpRequest = httpRequest;
  }

  public Request<?> getRequest() {
    return this.request;
  }

  public HttpRequestBase getHttpRequest() {
    return this.httpRequest;
  }

  public Map<String, String> getHeaders() {
    return this.headers;
  }

  public void addHeader(String name, String value) {
    this.headers.put(name, value);
  }

  public InputStream getContent() {
    return this.content;
  }

  public void setContent(InputStream content) {
    this.content = content;
  }

  public String getStatusText() {
    return this.statusText;
  }

  public void setStatusText(String statusText) {
    this.statusText = statusText;
  }

  public int getStatusCode() {
    return this.statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }
}
