package com.autumn.model;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class SessionAttributeAware implements SessionAttribute {

  public HttpServletRequest getCurrentRequest() {
    return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
        .getRequest();
  }

  public SessionAttribute getSessionAttribute() {
    return new GatewaySessionAttributeAbstract(getCurrentRequest());
  }

  /**
   * 获取用户ID
   *
   * @return
   */
  @Override
  public String getUserId() {
    return getSessionAttribute().getUserId();
  }

  /**
   * 获取用户名
   *
   * @return
   */
  @Override
  public String getUserName() {
    return getSessionAttribute().getUserName();
  }

  /**
   * 获取用户登录名
   *
   * @return
   */
  @Override
  public String getLoginName() {
    return getSessionAttribute().getLoginName();
  }

  /**
   * 获取手机号
   *
   * @return
   */
  @Override
  public String getMobile() {
    return getSessionAttribute().getMobile();
  }

  /**
   * 获取用户头像地址
   *
   * @return
   */
  @Override
  public String getUserPicUrl() {
    return getSessionAttribute().getUserPicUrl();
  }

  /**
   * 获取系统标识
   *
   * @return
   */
  @Override
  public Integer getSysFlag() {
    return getSessionAttribute().getSysFlag();
  }

  @Override
  public List<String> getResourceCodes() {
    return getSessionAttribute().getResourceCodes();
  }
}
