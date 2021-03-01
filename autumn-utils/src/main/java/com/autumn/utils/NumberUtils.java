package com.autumn.utils;

public class NumberUtils {

  public static Long parseLong(Object text) {

    return Long.parseLong(String.valueOf(text));
  }

  public static Long parseLong(Object text, Long def) {
    if (text == null) {
      return def;
    }
    try {
      return Long.parseLong(String.valueOf(text));
    } catch (NumberFormatException ex) {
      return def;
    }
  }

  public static Byte parseByte(Object text) {

    return Byte.parseByte(String.valueOf(text));
  }

  public static Byte parseByte(Object text, Byte def) {
    if (text == null) {
      return def;
    }
    try {
      return Byte.parseByte(String.valueOf(text));
    } catch (NumberFormatException ex) {
      return def;
    }
  }
}
