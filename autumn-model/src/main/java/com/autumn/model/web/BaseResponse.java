package com.autumn.model.web;

import com.autumn.model.enums.CommonEnum;
import com.autumn.model.enums.ResultCode;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.ThreadContext;

import java.util.Objects;

/**
 * @description: <公共响应类>
 * @author: qius
 * @create: 2019-08-07 10:31
 **/
@Data
@Accessors(chain = true)
@ToString
public class BaseResponse<T> implements BaseResult<T> {

    /**
     * 错误码
     */
    private int code;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 流水号
     */
    private String requestId;

    /**
     * 数据
     */
    private T data;

    public BaseResponse() {
        this.requestId = ThreadContext.get("X-B3-TraceId");
    }

    public BaseResponse(CommonEnum resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getName();
        this.requestId = ThreadContext.get("X-B3-TraceId");
    }

    public BaseResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.requestId = ThreadContext.get("X-B3-TraceId");
    }

    public BaseResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.requestId = ThreadContext.get("X-B3-TraceId");
    }

    public BaseResponse(T data) {
        this.code = ResultCode.SUCCESS.getCode();
        this.msg = ResultCode.SUCCESS.getName();
        this.data = data;
        this.requestId = ThreadContext.get("X-B3-TraceId");
    }

    public boolean resultDataIsNull() {
        return Objects.isNull(this.data);
    }

    public boolean resultDataNotNull() {
        return Objects.nonNull(this.data);
    }

    /**
     * 使用isXxx在序列号时会解析成Json，故前面加result
     */
    public boolean resultIs200() {
        return this.code == 200;
    }

    public boolean resultIs404() {
        return this.code == 404;
    }

    public boolean resultIs500() {
        return this.code == 500;
    }

    public boolean resultNot200() {
        return this.code != 200;
    }

    public boolean resultNot404() {
        return this.code != 404;
    }

    public boolean resultNot500() {
        return this.code != 500;
    }

    public boolean resultIs2xx() {
        return this.code >= 200 && this.code < 300;
    }

    public boolean resultIs4xx() {
        return this.code >= 400 && this.code < 500;
    }

    public boolean resultIs5xx() {
        return this.code >= 500 && this.code < 600;
    }
}
