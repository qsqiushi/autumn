//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autu.api.sign.sdk.auth.signer.internal;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class SignerUtils {
    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyyMMdd").withZoneUTC();
    private static final DateTimeFormatter timeFormatter =
        DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss'Z'").withZoneUTC();

    public SignerUtils() {}

    public static String formatDateStamp(long timeMilli) {
        return dateFormatter.print(timeMilli);
    }

    public static String formatTimestamp(long timeMilli) {
        return timeFormatter.print(timeMilli);
    }

    public static long parseMillis(String signDate) {
        return timeFormatter.parseMillis(signDate);
    }

}
