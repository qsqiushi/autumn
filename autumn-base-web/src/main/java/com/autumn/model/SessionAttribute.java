package com.autumn.model;

import java.io.Serializable;
import java.util.List;

/**
 * @description: <>
 * @author: qius
 * @create: 2019-08-07 15:33
 */
public interface SessionAttribute extends Serializable {

  /**
   * 获取用户ID
   *
   * @return
   */
  String getUserId();

  /**
   * 获取用户名
   *
   * @return
   */
  String getUserName();

  /**
   * 获取用户登录名
   *
   * @return
   */
  String getLoginName();

  /**
   * 获取手机号
   *
   * @return
   */
  String getMobile();

  /**
   * 获取用户头像地址
   *
   * @return
   */
  String getUserPicUrl();

  /**
   * 获取系统标识
   *
   * @return
   */
  Integer getSysFlag();

  /**
   * 获取资源编码
   *
   * @return
   */
  List<String> getResourceCodes();
}
