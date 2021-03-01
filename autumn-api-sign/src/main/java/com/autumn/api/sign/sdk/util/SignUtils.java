package com.autumn.api.sign.sdk.util;

import com.autumn.api.sign.apigateway.sdk.utils.Client;
import com.autumn.api.sign.apigateway.sdk.utils.Request;
import com.autumn.api.sign.sdk.auth.vo.SignResult;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.HashMap;
import java.util.Map;

public class SignUtils {
  public SignUtils() {}

  public static SignResult sign(Request request) throws Exception {
    SignResult result = new SignResult();
    HttpRequestBase signedRequest = Client.sign(request);
    Header[] headers = signedRequest.getAllHeaders();
    Map<String, String> headerMap = new HashMap();
    Header[] var5 = headers;
    int var6 = headers.length;

    for (int var7 = 0; var7 < var6; ++var7) {
      Header header = var5[var7];
      headerMap.put(header.getName(), header.getValue());
    }

    result.setUrl(signedRequest.getURI().toURL());
    result.setHeaders(headerMap);
    return result;
  }
}
