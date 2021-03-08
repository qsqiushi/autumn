package com.autumn.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** @ClassName: TradeBusinessException @Description: 统一定义异常抽象 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /** 错误码 */
  private int code;

  /** 错误信息 */
  private String msg;

  public BusinessException(int code, String msg) {
    super(String.valueOf(msg));
    this.code = code;
    this.msg = msg;
  }
}
