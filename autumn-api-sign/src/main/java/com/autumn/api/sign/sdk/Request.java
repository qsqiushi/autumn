//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autumn.api.sign.sdk;

import com.autumn.api.sign.sdk.http.HttpMethodName;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

public interface Request<T> {
  void addHeader(String var1, String var2);

  Map<String, String> getHeaders();

  void setHeaders(Map<String, String> var1);

  String getResourcePath();

  void setResourcePath(String var1);

  void addParameter(String var1, String var2);

  void addParameters(String var1, List<String> var2);

  Request<T> withParameter(String var1, String var2);

  Map<String, List<String>> getParameters();

  void setParameters(Map<String, List<String>> var1);

  URI getEndpoint();

  void setEndpoint(URI var1);

  HttpMethodName getHttpMethod();

  void setHttpMethod(HttpMethodName var1);

  InputStream getContent();

  void setContent(InputStream var1);

  WebServiceRequest getOriginalRequest();

  int getTimeOffset();

  void setTimeOffset(int var1);

  Request<T> withTimeOffset(int var1);
}
