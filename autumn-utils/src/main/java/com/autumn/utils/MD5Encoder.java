package com.autumn.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author @ClassName: MD5Encoder @Description: MD5操作工具类
 * @date 2014-08-07
 */
public class MD5Encoder {

  private static Logger logger = LoggerFactory.getLogger(MD5Encoder.class);

  /**
   * @param input
   * @return String
   * @throws @Title: encode @Description: MD5加密算法
   */
  public static String encode(String input) {
    if (StringUtils.isEmpty(input)) {
      return null;
    }
    byte[] digesta = null;
    try {
      MessageDigest alga = MessageDigest.getInstance("MD5");
      alga.update(input.getBytes(StandardCharsets.UTF_8));
      digesta = alga.digest();
    } catch (NoSuchAlgorithmException e) {
      logger.error("MD5加密异常", e);
      return null;
    }
    return byte2hex(digesta);
  }

  /** @name byte2hex @Description 加密输出为小写 */
  private static String byte2hex(byte[] b) {
    StringBuffer hs = new StringBuffer();
    String stmp = "";
    for (int n = 0; n < b.length; n++) {
      stmp = (Integer.toHexString(b[n] & 0XFF));
      if (stmp.length() == 1) {
        hs.append("0").append(stmp);
      } else {
        hs.append(stmp);
      }
    }
    return hs.toString().toLowerCase();
  }
}
