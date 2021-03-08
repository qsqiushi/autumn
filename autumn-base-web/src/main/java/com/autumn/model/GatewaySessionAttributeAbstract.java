package com.autumn.model;

import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @description: <>
 * @author: qius
 * @create: 2019-08-07 10:31
 */
public class GatewaySessionAttributeAbstract extends AbstractHeaderBasedAttribute
    implements SessionAttribute {

  @Getter private HttpServletRequest request;

  public GatewaySessionAttributeAbstract(HttpServletRequest request) {
    this.request = request;
  }

  /**
   * 获取用户ID
   *
   * @return
   */
  @Override
  public String getUserId() {
    return getValue("userId", String.class);
  }

  @Override
  public String getUserName() {
    return getValue("userName", String.class);
  }

  /**
   * 获取用户登录名
   *
   * @return
   */
  @Override
  public String getLoginName() {
    return getValue("loginName", String.class);
  }

  /**
   * 获取手机号
   *
   * @return
   */
  @Override
  public String getMobile() {
    return getValue("mobile", String.class);
  }

  /**
   * 获取用户头像地址
   *
   * @return
   */
  @Override
  public String getUserPicUrl() {
    return getValue("userPicUrl", String.class);
  }

  /**
   * 获取系统标识
   *
   * @return
   */
  @Override
  public Integer getSysFlag() {
    return getValue("sysFlag", Integer.class);
  }

  /**
   * 获取资源编码
   *
   * @return
   */
  @Override
  public List<String> getResourceCodes() {
    String resourceCodes = getValue("resourceCodes", String.class);
    if (resourceCodes == null || resourceCodes.length() == 0) {
      return null;
    }
    return Arrays.asList(resourceCodes.split(","));
  }
}
