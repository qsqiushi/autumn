//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autu.api.sign.sdk.util;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Locale;

public class BinaryUtils {
    public BinaryUtils() {}

    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        byte[] var2 = data;
        int var3 = data.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            byte b = var2[var4];
            String hex = Integer.toHexString(b);
            if (hex.length() == 1) {
                sb.append("0");
            } else if (hex.length() == 8) {
                hex = hex.substring(6);
            }

            sb.append(hex);
        }

        return sb.toString().toLowerCase(Locale.getDefault());
    }

    public static byte[] fromHex(String hexData) {
        byte[] result = new byte[(hexData.length() + 1) / 2];
        String hexNumber = null;
        int stringOffset = 0;

        for (int var4 = 0; stringOffset < hexData.length(); result[var4++] = (byte)Integer.parseInt(hexNumber, 16)) {
            hexNumber = hexData.substring(stringOffset, stringOffset + 2);
            stringOffset += 2;
        }

        return result;
    }

    /** @deprecated */
    @Deprecated
    public static ByteArrayInputStream toStream(ByteBuffer byteBuffer) {
        return byteBuffer == null ? null : new ByteArrayInputStream(copyBytesFrom(byteBuffer));
    }

    public static byte[] copyAllBytesFrom(ByteBuffer bb) {
        if (bb == null) {
            return null;
        } else if (bb.hasArray()) {
            return Arrays.copyOf(bb.array(), bb.limit());
        } else {
            bb.mark();
            int marked = bb.position();

            byte[] var3;
            try {
                byte[] dst = new byte[bb.rewind().remaining()];
                bb.get(dst);
                var3 = dst;
            } finally {
                bb.position(marked);
            }

            return var3;
        }
    }

    public static byte[] copyBytesFrom(ByteBuffer bb) {
        if (bb == null) {
            return null;
        } else if (bb.hasArray()) {
            return Arrays.copyOfRange(bb.array(), bb.position(), bb.limit());
        } else {
            bb.mark();

            byte[] var2;
            try {
                byte[] dst = new byte[bb.remaining()];
                bb.get(dst);
                var2 = dst;
            } finally {
                bb.reset();
            }

            return var2;
        }
    }
}
