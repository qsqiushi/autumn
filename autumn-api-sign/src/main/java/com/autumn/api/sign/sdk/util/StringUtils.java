//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autumn.api.sign.sdk.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;

public class StringUtils {
    private static final String DEFAULT_ENCODING = "UTF-8";
    public static final String COMMA_SEPARATOR = ",";
    public static final Charset UTF8 = Charset.forName("UTF-8");

    public StringUtils() {}

    public static Integer toInteger(StringBuilder value) {
        return Integer.parseInt(value.toString());
    }

    public static String toString(StringBuilder value) {
        return value.toString();
    }

    public static Boolean toBoolean(StringBuilder value) {
        return Boolean.valueOf(value.toString());
    }

    public static String fromInteger(Integer value) {
        return Integer.toString(value);
    }

    public static String fromLong(Long value) {
        return Long.toString(value);
    }

    public static String fromString(String value) {
        return value;
    }

    public static String fromBoolean(Boolean value) {
        return Boolean.toString(value);
    }

    public static String fromBigInteger(BigInteger value) {
        return value.toString();
    }

    public static String fromBigDecimal(BigDecimal value) {
        return value.toString();
    }

    public static BigInteger toBigInteger(String s) {
        return new BigInteger(s);
    }

    public static BigDecimal toBigDecimal(String s) {
        return new BigDecimal(s);
    }

    public static String fromFloat(Float value) {
        return Float.toString(value);
    }

    public static String fromDouble(Double d) {
        return Double.toString(d);
    }

    public static String fromByte(Byte b) {
        return Byte.toString(b);
    }

    public static String replace(String originalString, String partToMatch, String replacement) {
        StringBuffer buffer = new StringBuffer(originalString.length());
        buffer.append(originalString);

        for (int indexOf = buffer.indexOf(partToMatch); indexOf != -1; indexOf = buffer.indexOf(partToMatch)) {
            buffer = buffer.replace(indexOf, indexOf + partToMatch.length(), replacement);
        }

        return buffer.toString();
    }

    public static String join(String joiner, String... parts) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < parts.length; ++i) {
            builder.append(parts[i]);
            if (i < parts.length - 1) {
                builder.append(joiner);
            }
        }

        return builder.toString();
    }

    public static String trim(String value) {
        return value == null ? null : value.trim();
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null ? true : value.isEmpty();
    }
}
