package com.autumn.api.sign.apigateway.sdk.utils;

/**
 * @program: airlook-build
 * @description:
 * @author: qius
 * @create: 2019-09-02:14:29
 **/
public enum CheckSignResult {


    SUCCESS(""),

    FAIL_HEADER_X_SDK_DATE("请求头缺少X-Sdk-Date，或X-Sdk-Date格式不正确"),

    FAIL_SIGN_EXCEPTION("签名异常"),

    FAIL_TIMEOUT("签名超时"),

    FAIL_MATCH("签名错误");


    private final String msg;

    private CheckSignResult(String msg) {
        this.msg = msg;
    }


}
 