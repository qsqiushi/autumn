//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autu.api.sign.sdk;

import com.autu.api.sign.sdk.annotation.NotThreadSafe;

import java.util.EnumMap;

@NotThreadSafe
public final class RequestClientOptions {
    public static final int DEFAULT_STREAM_BUFFER_SIZE = 131073;

    private int readLimit = 131073;

    public static enum Marker {
        USER_AGENT;

        private Marker() {}
    }

    private final EnumMap<Marker, String> markers = new EnumMap(Marker.class);

    public RequestClientOptions() {}

    /** @deprecated */
    @Deprecated
    public String getClientMarker() {
        return this.getClientMarker(RequestClientOptions.Marker.USER_AGENT);
    }

    public String getClientMarker(RequestClientOptions.Marker marker) {
        return (String)this.markers.get(marker);
    }

    public void putClientMarker(RequestClientOptions.Marker marker, String value) {
        this.markers.put(marker, value);
    }

    /** @deprecated */
    @Deprecated
    public void addClientMarker(String clientMarker) {
        this.appendUserAgent(clientMarker);
    }

    public void appendUserAgent(String userAgent) {
        String marker = (String)this.markers.get(RequestClientOptions.Marker.USER_AGENT);
        if (marker == null) {
            marker = "";
        }

        marker = this.createUserAgentMarkerString(marker, userAgent);
        this.putClientMarker(RequestClientOptions.Marker.USER_AGENT, marker);
    }

    private String createUserAgentMarkerString(String marker, String userAgent) {
        return marker.contains(userAgent) ? marker : marker + " " + userAgent;
    }

    public final int getReadLimit() {
        return this.readLimit;
    }

    public final void setReadLimit(int readLimit) {
        this.readLimit = readLimit;
    }

    void copyTo(RequestClientOptions target) {
        target.setReadLimit(this.getReadLimit());
        RequestClientOptions.Marker[] var2 = RequestClientOptions.Marker.values();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            RequestClientOptions.Marker marker = var2[var4];
            target.putClientMarker(marker, this.getClientMarker(marker));
        }

    }
}
