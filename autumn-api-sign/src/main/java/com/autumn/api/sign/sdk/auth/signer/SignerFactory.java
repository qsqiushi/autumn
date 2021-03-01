package com.autumn.api.sign.sdk.auth.signer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SignerFactory {
  private static final String DEFAULT_SIGNER = "DefaultSignerType";
  private static final Map<String, Class<? extends Signer>> SIGNERS = new ConcurrentHashMap();

  static {
    SIGNERS.put("DefaultSignerType", DefaultSigner.class);
  }

  private SignerFactory() {}

  public static void registerSigner(String signerType, Class<? extends Signer> signerClass) {
    if (signerType == null) {
      throw new IllegalArgumentException("signerType cannot be null");
    } else if (signerClass == null) {
      throw new IllegalArgumentException("signerClass cannot be null");
    } else {
      SIGNERS.put(signerType, signerClass);
    }
  }

  public static Signer getSigner(String serviceName, String regionName) {
    return createSigner("DefaultSignerType", serviceName, regionName);
  }

  public static Signer getSigner() {
    return getSigner("", "");
  }

  public static Signer getSignerByTypeAndService(String signerType, String serviceName) {
    return createSigner(signerType, serviceName, (String) null);
  }

  private static Signer createSigner(String signerType, String serviceName, String regionName) {
    Class<? extends Signer> signerClass = (Class) SIGNERS.get(signerType);
    if (signerClass == null) {
      throw new IllegalArgumentException("unknown signer type: " + signerType);
    } else {
      try {
        Signer signer = (Signer) signerClass.newInstance();
        return signer;
      } catch (InstantiationException var6) {
        throw new IllegalStateException(
            "Cannot create an instance of " + signerClass.getName(), var6);
      } catch (IllegalAccessException var7) {
        throw new IllegalStateException(
            "Cannot create an instance of " + signerClass.getName(), var7);
      }
    }
  }
}
