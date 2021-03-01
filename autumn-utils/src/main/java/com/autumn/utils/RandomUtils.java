package com.autumn.utils;

import java.util.Random;

/**
 * 获得随机数
 *
 * @author Aysn
 * @since 2019-05-30
 */
public class RandomUtils {

  /**
   * 在一个区间随机获得一个数
   *
   * @param start start number
   * @param end end number
   * @return 含头不含尾
   */
  public static int randomRange(int start, int end) {
    return start + (int) (Math.random() * end - start);
  }

  /**
   * 获取指定位数的随机数(纯数字)
   *
   * <p>可以用于获得验证码
   *
   * @param length 随机数的位数
   * @return String
   */
  public static String numberString(int length) {
    if (length <= 0) {
      length = 1;
    }
    StringBuffer res = new StringBuffer();
    Random random = new Random();
    int i = 0;
    while (i < length) {
      res.append(random.nextInt(10));
      i++;
    }
    return res.toString();
  }

  public static void main(String[] args) {
    System.out.println(numberString(6));
  }
}
