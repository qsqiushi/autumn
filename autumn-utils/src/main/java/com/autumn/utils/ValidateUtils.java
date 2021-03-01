package com.autumn.utils;

import java.util.regex.Pattern;

/**
 * 请求参数校验工具类
 *
 * @author Aysn
 */
public class ValidateUtils {

  private static final String PHONE_REGEX = "^1\\d{10}$";

  private static final String EMAIL_REGEX =
      "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";

  /** 必须同时包含数字和字母，长度在6-16位 */
  private static final String PWD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)\\S{6,16}$";

  /**
   * 校验密码
   *
   * @param password
   * @return
   */
  public static boolean isPassword(String password) {
    return Pattern.matches(PWD_REGEX, password);
  }

  /**
   * 手机号码校验
   *
   * @param phone
   * @return
   */
  public static boolean isPhone(String phone) {
    if (phone == null || phone.trim().length() == 0) {
      return false;
    }
    return Pattern.matches(PHONE_REGEX, phone);
  }

  /**
   * 邮箱校验
   *
   * @param email
   * @return
   */
  public static boolean isEmail(String email) {
    if (email == null || email.trim().length() == 0) {
      return false;
    }
    return Pattern.matches(EMAIL_REGEX, email);
  }
}
