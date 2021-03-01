package com.autumn.model.enums;

import lombok.Getter;

/** @ClassName: ResultCode @Description: 返回状态码 */
@Getter
public enum ResultCode implements CommonEnum<ResultCode> {
  SUCCESS(200, "成功"),

  BAD_REQUEST(400, "请求参数不合理或为空"),

  NOT_LOGIN(401, "用户登录未登录或已过期，请重新登录"),

  NOT_FORBIDDEN(403, "没有权限操作"),

  NOT_FIND(404, "没有找到对应信息"),

  NOT_SUPPORT_OPT(409, "当期状态不支持此类型操作"),

  SVR_INNER_ERROR(500, "服务内部异常，请稍后重试或联系客服"),

  FEIGN_ERROR(600, "请求调用失败"),

  GATEWAY_ERROR(601, "请求调用失败"),

  GATEWAY_LIMIT_ERROR(602, "访问过快"),
  ;

  private final int code;

  private final String name;

  private ResultCode(int code, String name) {
    this.code = code;
    this.name = name;
  }

  public static ResultCode valueOf(int code) {
    for (ResultCode type : ResultCode.values()) {
      if (type.getCode() == code) {
        return type;
      }
    }
    return SVR_INNER_ERROR;
  }
}
