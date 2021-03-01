package com.autumn.api.sign.sdk.http;

import com.autumn.api.sign.sdk.ClientConfiguration;
import com.autumn.api.sign.sdk.ClientException;
import com.autumn.api.sign.sdk.Request;
import com.autumn.api.sign.sdk.util.HttpUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Iterator;
import java.util.Map.Entry;

class HttpRequestFactory {
  private static final String DEFAULT_ENCODING = "UTF-8";

  HttpRequestFactory() {}

  HttpRequestBase createHttpRequest(
      Request<?> request, ClientConfiguration clientConfiguration, ExecutionContext context) {
    URI endpoint = request.getEndpoint();
    String uri = HttpUtils.appendUri(endpoint.toString(), request.getResourcePath(), true);
    String encodedParams = HttpUtils.encodeParameters(request);
    boolean requestHasNoPayload = request.getContent() != null;
    boolean requestIsPost = request.getHttpMethod() == HttpMethodName.POST;
    boolean putParamsInUri = !requestIsPost || requestHasNoPayload;
    if (encodedParams != null && putParamsInUri) {
      uri = uri + "?" + encodedParams;
    }

    Object httpRequest;
    if (request.getHttpMethod() == HttpMethodName.POST) {
      HttpPost postMethod = new HttpPost(uri);
      if (request.getContent() == null && encodedParams != null) {
        postMethod.setEntity(this.newStringEntity(encodedParams));
      } else {
        postMethod.setEntity(new RepeatableInputStreamRequestEntity(request));
      }

      httpRequest = postMethod;
    } else {
      Object entity;
      if (request.getHttpMethod() == HttpMethodName.PUT) {
        HttpPut putMethod = new HttpPut(uri);
        httpRequest = putMethod;
        putMethod.setConfig(RequestConfig.custom().setExpectContinueEnabled(true).build());
        if (request.getContent() != null) {
          entity = new RepeatableInputStreamRequestEntity(request);
          if (request.getHeaders().get("Content-Length") == null) {
            entity = this.newBufferedHttpEntity((HttpEntity) entity);
          }

          putMethod.setEntity((HttpEntity) entity);
        }
      } else if (request.getHttpMethod() == HttpMethodName.PATCH) {
        HttpPatch patchMethod = new HttpPatch(uri);
        httpRequest = patchMethod;
        if (request.getContent() != null) {
          entity = new RepeatableInputStreamRequestEntity(request);
          if (request.getHeaders().get("Content-Length") == null) {
            entity = this.newBufferedHttpEntity((HttpEntity) entity);
          }

          patchMethod.setEntity((HttpEntity) entity);
        }
      } else if (request.getHttpMethod() == HttpMethodName.GET) {
        httpRequest = new HttpGet(uri);
      } else if (request.getHttpMethod() == HttpMethodName.DELETE) {
        httpRequest = new HttpDelete(uri);
      } else {
        if (request.getHttpMethod() != HttpMethodName.HEAD) {
          throw new ClientException("Unknown HTTP method name: " + request.getHttpMethod());
        }

        httpRequest = new HttpHead(uri);
      }
    }

    this.configureHeaders((HttpRequestBase) httpRequest, request, context, clientConfiguration);
    return (HttpRequestBase) httpRequest;
  }

  private void configureHeaders(
      HttpRequestBase httpRequest,
      Request<?> request,
      ExecutionContext context,
      ClientConfiguration clientConfiguration) {
    URI endpoint = request.getEndpoint();
    String hostHeader = endpoint.getHost();
    if (HttpUtils.isUsingNonDefaultPort(endpoint)) {
      hostHeader = hostHeader + ":" + endpoint.getPort();
    }

    httpRequest.addHeader("Host", hostHeader);
    Iterator var7 = request.getHeaders().entrySet().iterator();

    while (var7.hasNext()) {
      Entry<String, String> entry = (Entry) var7.next();
      if (!((String) entry.getKey()).equalsIgnoreCase("Content-Length")
          && !((String) entry.getKey()).equalsIgnoreCase("Host")) {
        httpRequest.addHeader((String) entry.getKey(), (String) entry.getValue());
      }
    }

    if (httpRequest.getHeaders("Content-Type") == null
        || httpRequest.getHeaders("Content-Type").length == 0) {
      httpRequest.addHeader(
          "Content-Type", "application/x-www-form-urlencoded; charset=" + "UTF-8".toLowerCase());
    }

    if (context != null && context.getContextUserAgent() != null) {
      httpRequest.addHeader(
          "User-Agent",
          this.createUserAgentString(clientConfiguration, context.getContextUserAgent()));
    }
  }

  private String createUserAgentString(
      ClientConfiguration clientConfiguration, String contextUserAgent) {
    return clientConfiguration.getUserAgent().contains(contextUserAgent)
        ? clientConfiguration.getUserAgent()
        : clientConfiguration.getUserAgent() + " " + contextUserAgent;
  }

  private HttpEntity newStringEntity(String s) {
    try {
      return new StringEntity(s);
    } catch (UnsupportedEncodingException var3) {
      throw new ClientException("Unable to create HTTP entity: " + var3.getMessage(), var3);
    }
  }

  private HttpEntity newBufferedHttpEntity(HttpEntity entity) {
    try {
      return new BufferedHttpEntity(entity);
    } catch (IOException var3) {
      throw new ClientException("Unable to create HTTP entity: " + var3.getMessage(), var3);
    }
  }
}
