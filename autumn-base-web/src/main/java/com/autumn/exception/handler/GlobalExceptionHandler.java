package com.autumn.exception.handler;

import com.autumn.exception.BusinessException;
import com.autumn.model.enums.ResultCode;
import com.autumn.model.web.BaseResponse;
import com.autumn.model.web.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * <全局拦截异常统一处理>
 *
 * @author qiushi
 * @since 2021/3/8 14:26
 */
@Slf4j
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

  /**
   * 业务异常返回
   *
   * @param ex 异常
   * @return Result
   */
  @ExceptionHandler(BusinessException.class)
  public BaseResponse businessHandler(BusinessException ex) {
    log.error(ex.getMessage(), ex);
    return ResponseBuilder.fail(ex.getCode(), ex.getMsg());
  }

  /**
   * @param ex Result
   * @return Result
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public BaseResponse validHandler(MethodArgumentNotValidException ex) {
    log.error(ex.getMessage(), ex);
    BindingResult bindingResult = ex.getBindingResult();
    if (!bindingResult.hasErrors()) {
      return ResponseBuilder.success();
    }
    // 返回最前错误即可，没必要都返回
    final String msg =
        bindingResult.getAllErrors().stream()
            .findFirst()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .orElse(null);

    if (msg == null || msg.length() == 0) {
      return ResponseBuilder.badRequest();
    } else {
      return ResponseBuilder.badRequest(msg);
    }
  }

  /**
   * 处理参数异常类型错误
   *
   * @param ex 异常
   * @return Result
   */
  @ExceptionHandler({
    // 处理参数异常类型错误
    Exception.class,
  })
  public BaseResponse is4xxHandler(Throwable ex) {
    log.error(ex.getMessage(), ex);
    return ResponseBuilder.badRequest(
        (ex.getMessage() == null || ex.getMessage().length() == 0)
            ? ResultCode.BAD_REQUEST.getName()
            : ex.getMessage());
  }

  /**
   * 主要处理服务错误与参数异常
   *
   * @param ex 异常
   * @return Result
   */
  @ExceptionHandler(Throwable.class)
  public BaseResponse defaultHandler(Throwable ex) {
    log.error(ex.getMessage(), ex);
    return ResponseBuilder.serverError();
  }
}
