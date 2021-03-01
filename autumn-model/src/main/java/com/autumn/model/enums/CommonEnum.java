package com.autumn.model.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 公共枚举接口
 *
 * @param <E>
 * @author shenyonggang
 */
public interface CommonEnum<E> {

  /**
   * 获取枚举值对应的枚举
   *
   * @param enumClass 枚举类
   * @param enumValue 枚举值
   * @return 枚举
   */
  static <E extends CommonEnum<E>> E getEnum(final Class<E> enumClass, final Integer enumValue) {
    if (enumValue == null) {
      return null;
    }
    try {
      return valueOf(enumClass, enumValue);
    } catch (final IllegalArgumentException ex) {
      return null;
    }
  }

  /**
   * 获取枚举值对应的枚举
   *
   * @param enumClass 枚举类
   * @param enumValue 枚举值
   * @return 枚举
   */
  static <E extends CommonEnum> E valueOf(Class<E> enumClass, Integer enumValue) {
    if (enumValue == null) {
      throw new NullPointerException("EnumValue is null");
    }
    return getEnumMap(enumClass).get(enumValue);
  }

  /**
   * 获取枚举键值对
   *
   * @param enumClass 枚举类型
   * @return 键值对
   */
  static <E extends CommonEnum> Map<Integer, E> getEnumMap(Class<E> enumClass) {
    E[] enums = enumClass.getEnumConstants();
    if (enums == null) {
      throw new IllegalArgumentException(
          enumClass.getSimpleName() + " does not represent an enum type.");
    }
    Map<Integer, E> map = new HashMap<>(2 * enums.length);
    for (E t : enums) {
      map.put(t.getCode(), t);
    }
    return map;
  }

  /**
   * 用于获得安全的code
   *
   * @param commonStatus
   * @return
   */
  static Integer getSafeCode(CommonEnum commonStatus) {
    if (Objects.isNull(commonStatus)) {
      return null;
    }
    return commonStatus.getCode();
  }

  /**
   * 获取code
   *
   * @return
   */
  int getCode();

  /**
   * 获取名称
   *
   * @return
   */
  String getName();
}
