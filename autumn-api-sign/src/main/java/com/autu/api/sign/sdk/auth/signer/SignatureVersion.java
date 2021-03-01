//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autu.api.sign.sdk.auth.signer;

public enum SignatureVersion {
    V1("1");

    private String value;

    private SignatureVersion(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
