//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autu.api.sign.sdk.util;

import com.autu.api.sign.sdk.internal.Releasable;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class IOUtils {
    private static final int BUFFER_SIZE = 4096;

    private IOUtils() {
    }

    public static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            byte[] b = new byte[4096];
            boolean var3 = false;

            int n;
            while ((n = is.read(b)) != -1) {
                output.write(b, 0, n);
            }

            byte[] var4 = output.toByteArray();
            return var4;
        } finally {
            output.close();
        }
    }

    public static String toString(InputStream is) throws IOException {
        return new String(toByteArray(is), StringUtils.UTF8);
    }

    public static void closeQuietly(Closeable is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException var4) {

                if (log.isDebugEnabled()) {
                    log.debug("Ignore failure in closing the Closeable", var4);
                }
            }
        }

    }

    public static void release(Closeable is) {
        closeQuietly(is);
        if (is instanceof Releasable) {
            Releasable r = (Releasable) is;
            r.release();
        }

    }

    public static long copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4096];
        long count = 0L;

        int n;
        for (boolean var5 = false; (n = in.read(buf)) > -1; count += (long) n) {
            out.write(buf, 0, n);
        }

        return count;
    }
}
