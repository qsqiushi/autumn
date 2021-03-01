package com.autumn.api.sign.apigateway.sdk.utils;

import com.autumn.api.sign.sdk.DefaultRequest;
import com.autumn.api.sign.sdk.Request;
import com.autumn.api.sign.sdk.auth.credentials.BasicCredentials;
import com.autumn.api.sign.sdk.auth.signer.Signer;
import com.autumn.api.sign.sdk.auth.signer.SignerFactory;
import com.autumn.api.sign.sdk.http.HttpMethodName;
import org.apache.http.Header;
import org.apache.http.client.methods.*;
import org.apache.http.entity.InputStreamEntity;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;

public class AccessServiceImpl extends AccessService {
  public AccessServiceImpl(String ak, String sk) {
    super(ak, sk);
  }

  private static HttpRequestBase createRequest(
      String url,
      Header header,
      InputStream content,
      Long contentLength,
      HttpMethodName httpMethod) {
    Object httpRequest;
    InputStreamEntity entity;
    if (httpMethod == HttpMethodName.POST) {
      HttpPost postMethod = new HttpPost(url.toString());
      if (content != null) {
        entity = new InputStreamEntity(content, contentLength);
        postMethod.setEntity(entity);
      }

      httpRequest = postMethod;
    } else if (httpMethod == HttpMethodName.PUT) {
      HttpPut putMethod = new HttpPut(url.toString());
      httpRequest = putMethod;
      if (content != null) {
        entity = new InputStreamEntity(content, contentLength);
        putMethod.setEntity(entity);
      }
    } else if (httpMethod == HttpMethodName.PATCH) {
      HttpPatch patchMethod = new HttpPatch(url.toString());
      httpRequest = patchMethod;
      if (content != null) {
        entity = new InputStreamEntity(content, contentLength);
        patchMethod.setEntity(entity);
      }
    } else if (httpMethod == HttpMethodName.GET) {
      httpRequest = new HttpGet(url.toString());
    } else if (httpMethod == HttpMethodName.DELETE) {
      httpRequest = new HttpDelete(url.toString());
    } else if (httpMethod == HttpMethodName.OPTIONS) {
      httpRequest = new HttpOptions(url.toString());
    } else {
      if (httpMethod != HttpMethodName.HEAD) {
        throw new RuntimeException("Unknown HTTP method name: " + httpMethod);
      }

      httpRequest = new HttpHead(url.toString());
    }

    ((HttpRequestBase) httpRequest).addHeader(header);
    return (HttpRequestBase) httpRequest;
  }

  @Override
  public HttpRequestBase access(
      String url,
      Map<String, String> headers,
      InputStream content,
      Long contentLength,
      HttpMethodName httpMethod)
      throws Exception {
    Request request = new DefaultRequest();
    String host = "";
    String queryString = "";

    String p;
    try {
      if (url.contains("?")) {
        host = url.substring(0, url.indexOf("?"));
        queryString = url.substring(url.indexOf("?") + 1);
      } else {
        host = url;
      }

      request.setEndpoint((new URL(host)).toURI());
      if (url.contains("?") && null != queryString && !"".equals(queryString)) {
        String[] parameterarray = queryString.split("&");
        String[] var10 = parameterarray;
        int var11 = parameterarray.length;

        for (int var12 = 0; var12 < var11; ++var12) {
          p = var10[var12];
          String[] p_split = p.split("=", 2);
          String key = p_split[0];
          String value = "";
          if (p_split.length >= 2) {
            value = p_split[1];
          }

          request.addParameter(URLDecoder.decode(key, "UTF-8"), URLDecoder.decode(value, "UTF-8"));
        }
      }
    } catch (URISyntaxException var17) {
      var17.printStackTrace();
    }

    request.setHttpMethod(httpMethod);
    if (headers != null) {
      request.setHeaders(headers);
    }

    request.setContent(content);
    Signer signer = SignerFactory.getSigner();
    signer.sign(request, new BasicCredentials(this.ak, this.sk));
    HttpRequestBase httpRequestBase =
        createRequest(url, (Header) null, request.getContent(), contentLength, httpMethod);
    Map<String, String> requestHeaders = request.getHeaders();
    Iterator var21 = requestHeaders.keySet().iterator();

    while (var21.hasNext()) {
      p = (String) var21.next();
      if (!p.equalsIgnoreCase("Content-Length".toString())) {
        String value = (String) requestHeaders.get(p);
        httpRequestBase.addHeader(p, new String(value.getBytes("UTF-8"), "ISO-8859-1"));
      }
    }

    return httpRequestBase;
  }
}
