package com.autumn.model;

import com.autumn.model.enums.CommonEnum;
import com.autumn.utils.NumberUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @description: <>
 * @author: qius
 * @create: 2019-08-07 10:51
 */
public abstract class AbstractHeaderBasedAttribute {
  public static String urlDecode(String str) {
    try {
      return URLDecoder.decode(str, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  /**
   * <获取request>
   *
   * @param: []
   * @return: javax.servlet.http.HttpServletRequest
   * @author: qius
   * @updator: qius
   * @date: 2019-08-07 10:38
   */
  public abstract HttpServletRequest getRequest();

  protected String getValue(String header) {
    return getValue(header, String.class);
  }

  protected <T> T getValue(String header, Class<T> type) {
    String headerValue = getRequest().getHeader(header);
    if (headerValue == null) {
      return null;
    }
    headerValue = urlDecode(headerValue);
    if (type == Long.class) {
      return (T) NumberUtils.parseLong(headerValue, null);
    }
    if (type == Byte.class) {
      return (T) NumberUtils.parseByte(headerValue, null);
    }
    if (type == String.class) {
      return (T) headerValue;
    }

    if (type == Integer.class) {
      if (headerValue == null || headerValue.length() == 0) {
        return null;
      } else {

        return (T) Integer.valueOf(Integer.parseInt(headerValue));
      }
    }
    if (type.isEnum()) {}

    throw new IllegalArgumentException("unsupported type " + type);
  }

  /**
   * 获取Enum类型，需实现CommonEnum
   *
   * @param header
   * @param type
   * @param <E>
   * @return
   */
  protected <E extends CommonEnum<E>> E getEnumValue(String header, Class<E> type) {

    String headerValue = getRequest().getHeader(header);
    if (headerValue == null) {
      return null;
    }
    headerValue = urlDecode(headerValue);

    if (type.isEnum() && CommonEnum.class.isAssignableFrom(type)) {
      return CommonEnum.getEnum(type, Integer.valueOf(headerValue));
    }
    throw new IllegalArgumentException("unsupported type " + type);
  }
}
