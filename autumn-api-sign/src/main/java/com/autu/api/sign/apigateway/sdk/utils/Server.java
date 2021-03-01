package com.autu.api.sign.apigateway.sdk.utils;

import com.autu.api.sign.sdk.DefaultRequest;
import com.autu.api.sign.sdk.Request;
import com.autu.api.sign.sdk.auth.credentials.BasicCredentials;
import com.autu.api.sign.sdk.auth.signer.Signer;
import com.autu.api.sign.sdk.auth.signer.SignerFactory;
import com.autu.api.sign.sdk.auth.signer.internal.SignerUtils;
import com.autu.api.sign.sdk.http.HttpMethodName;
import com.autu.api.sign.sdk.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.time.LocalDateTime;

/**
 * @program:
 * @description:
 * @author: qius
 * @create: 2019-08-31:14:18
 **/
@Slf4j
public class Server {


    public static CheckSignResult check(String appKey, String appSecret, HttpServletRequest request,
                                        HttpMethodName httpMethodName, Long time) {

        String xSdkDate = request.getHeader("X-Sdk-Date");
        if (StringUtils.isNullOrEmpty(xSdkDate)) {
            return CheckSignResult.FAIL_SIGN_EXCEPTION;
        }
        Long xSdkDateMillis = null;
        try {
            xSdkDateMillis = SignerUtils.parseMillis(xSdkDate);
        } catch (Exception ex) {
            return CheckSignResult.FAIL_SIGN_EXCEPTION;
        }


        String sign = null;
        try {
            sign = sign(appKey, appSecret, request, httpMethodName);
        } catch (Exception e) {
            log.error("签名出现异常:[{}]", e);
        }

        if (sign == null) {
            return CheckSignResult.FAIL_SIGN_EXCEPTION;
        }

        LocalDateTime localDateTime = LocalDateTime.now();


        return null;

    }


    public static String sign(String appKey, String appSecret, HttpServletRequest request,
                              HttpMethodName httpMethodName) throws Exception {

        switch (httpMethodName) {
            case GET:
                return get(appKey, appSecret, request);
            case POST:
                return post(appKey, appSecret, request);
            case PUT:
                return put(appKey, appSecret, request);
            case PATCH:
                return patch(appKey, appSecret, request);
            case DELETE:
                return delete(appKey, appSecret, request);
            case HEAD:
                return head(appKey, appSecret, request);
            case OPTIONS:
                return options(appKey, appSecret, request);
        }
        throw new IllegalArgumentException(
                String.format("unsupported method:%s", new Object[]{httpMethodName.name()}));
    }

    private static String head(String appKey, String appSecret, HttpServletRequest httpServletRequest) {

        HttpMethodName httpMethod = HttpMethodName.PATCH;

        return getSign(appKey, appSecret, httpServletRequest, httpMethod);
    }

    private static String options(String appKey, String appSecret, HttpServletRequest httpServletRequest) {

        HttpMethodName httpMethod = HttpMethodName.OPTIONS;

        return getSign(appKey, appSecret, httpServletRequest, httpMethod);
    }

    private static String post(String appKey, String appSecret, HttpServletRequest httpServletRequest) {

        HttpMethodName httpMethod = HttpMethodName.POST;

        return getSign(appKey, appSecret, httpServletRequest, httpMethod);

    }

    private static String patch(String appKey, String appSecret, HttpServletRequest httpServletRequest) {

        HttpMethodName httpMethod = HttpMethodName.PATCH;

        return getSign(appKey, appSecret, httpServletRequest, httpMethod);

    }

    private static String put(String appKey, String appSecret, HttpServletRequest httpServletRequest) {

        HttpMethodName httpMethod = HttpMethodName.PUT;

        return getSign(appKey, appSecret, httpServletRequest, httpMethod);

    }

    public static String get(String ak, String sk, HttpServletRequest httpServletRequest) {

        HttpMethodName httpMethod = HttpMethodName.GET;

        return getSign(ak, sk, httpServletRequest, httpMethod);

    }

    public static String delete(String ak, String sk, HttpServletRequest httpServletRequest) {

        HttpMethodName httpMethod = HttpMethodName.DELETE;

        return getSign(ak, sk, httpServletRequest, httpMethod);

    }

    private static String getSign(String appKey, String appSecret, HttpServletRequest httpServletRequest,
                                  HttpMethodName httpMethod) {
        try {

            Request request = new DefaultRequest();

            String host = httpServletRequest.getRequestURL().toString();

            String queryString = httpServletRequest.getQueryString();

            String p;

            if (!StringUtils.isNullOrEmpty(queryString)) {

                String[] parameterArray = queryString.split("&");

                int parameterArrayLength = parameterArray.length;

                for (int temp = 0; temp < parameterArrayLength; ++temp) {
                    p = parameterArray[temp];
                    String[] p_split = p.split("=", 2);
                    String key = p_split[0];
                    String value = "";
                    if (p_split.length >= 2) {
                        value = p_split[1];
                    }
                    request.addParameter(URLDecoder.decode(key, "UTF-8"), URLDecoder.decode(value, "UTF-8"));
                }
            }

            String str, wholeStr = "";

            BufferedReader br = httpServletRequest.getReader();

            if (br != null) {
                while ((str = br.readLine()) != null) {
                    wholeStr += str;
                }

                if (!StringUtils.isNullOrEmpty(wholeStr)) {
                    byte[] bytes = wholeStr.getBytes("UTF-8");

                    InputStream content = new ByteArrayInputStream(bytes);

                    request.setContent(content);
                }
            }
            request.setHttpMethod(httpMethod);

            request.setEndpoint((new URL(host)).toURI());

            if (httpServletRequest.getHeader("Content-Type") != null) {
                request.addHeader("Content-Type", httpServletRequest.getHeader("Content-Type"));
            } else {
                request.addHeader("Content-Type", "application/json;charset=UTF-8");
            }

            request.addHeader("X-Sdk-Date", httpServletRequest.getHeader("X-Sdk-Date"));

            Signer signer = SignerFactory.getSigner();

            signer.sign(request, new BasicCredentials(appKey, appSecret));

            return request.getHeaders().get("Authorization").toString();

        } catch (Exception ex) {

            ex.printStackTrace();
            return null;
        }
    }
}
