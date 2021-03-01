//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autu.api.sign.sdk;

public enum Protocol {
    HTTP("http"), HTTPS("https");

    private final String protocol;

    private Protocol(String protocol) {
        this.protocol = protocol;
    }

    public String toString() {
        return this.protocol;
    }
}
