
package com.autumn.api.sign.apigateway.sdk.utils;

import com.autumn.api.sign.sdk.DefaultRequest;
import com.autumn.api.sign.sdk.auth.credentials.BasicCredentials;
import com.autumn.api.sign.sdk.auth.signer.Signer;
import com.autumn.api.sign.sdk.auth.signer.SignerFactory;
import com.autumn.api.sign.sdk.http.HttpMethodName;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

public class AccessServiceOkhttpImpl extends AccessServiceOkhttp {
    public AccessServiceOkhttpImpl(String ak, String sk) {
        super(ak, sk);
    }

    @Override
    public Request access(String url, Map<String, String> headers, String entity, HttpMethodName httpMethod)
        throws Exception {
        com.autumn.api.sign.sdk.Request request = new DefaultRequest();
        String host = "";
        String queryString = "";
        try {
            if (url.contains("?")) {
                host = url.substring(0, url.indexOf("?"));
                queryString = url.substring(url.indexOf("?") + 1);
            } else {
                host = url;
            }

            request.setEndpoint(new URL(host).toURI());
            if ((url.contains("?")) && (null != queryString) && (!"".equals(queryString))) {
                String[] parameterarray = queryString.split("&");
                for (String p : parameterarray) {
                    String[] p_split = p.split("=", 2);
                    String key = p_split[0];
                    String value = "";
                    if (p_split.length >= 2) {
                        value = p_split[1];
                    }
                    request.addParameter(URLDecoder.decode(key, "UTF-8"), URLDecoder.decode(value, "UTF-8"));
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        request.setHttpMethod(httpMethod);
        if (headers != null) {
            request.setHeaders(headers);
        }
        if (entity != null) {
            InputStream content = new ByteArrayInputStream(entity.getBytes("UTF-8"));
            request.setContent(content);
        }
        Signer signer = SignerFactory.getSigner();
        signer.sign(request, new BasicCredentials(this.ak, this.sk));
        return createRequest(url, request.getHeaders(), entity, httpMethod);
    }

    private static Request createRequest(String url, Map<String, String> headers, String body,
        HttpMethodName httpMethod) throws Exception {
        RequestBody entity = RequestBody.create(MediaType.parse(""), body.getBytes("UTF-8"));
        Request httpRequest;
        if (httpMethod == HttpMethodName.POST) {
            httpRequest = new Request.Builder().url(url).post(entity).build();
        } else if (httpMethod == HttpMethodName.PUT) {
            httpRequest = new Request.Builder().url(url).put(entity).build();
        } else if (httpMethod == HttpMethodName.PATCH) {
            httpRequest = new Request.Builder().url(url).patch(entity).build();
        } else if (httpMethod == HttpMethodName.DELETE) {
            httpRequest = new Request.Builder().url(url).delete(entity).build();
        } else if (httpMethod == HttpMethodName.GET) {
            httpRequest = new Request.Builder().url(url).get().build();
        } else if (httpMethod == HttpMethodName.HEAD) {
            httpRequest = new Request.Builder().url(url).head().build();
        } else if (httpMethod == HttpMethodName.OPTIONS) {
            httpRequest = new Request.Builder().url(url).method("OPTIONS", null).build();
        } else {
            throw new RuntimeException("Unknown HTTP method name: " + httpMethod);
        }
        for (String key : headers.keySet()) {
            httpRequest = httpRequest.newBuilder().addHeader(key, (String)headers.get(key)).build();
        }
        return httpRequest;
    }
}
