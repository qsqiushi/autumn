package com.autumn.api.sign.sdk.auth.signer;

import com.autumn.api.sign.sdk.ClientException;
import com.autumn.api.sign.sdk.Request;
import com.autumn.api.sign.sdk.WebServiceRequest;
import com.autumn.api.sign.sdk.auth.credentials.BasicCredentials;
import com.autumn.api.sign.sdk.auth.credentials.Credentials;
import com.autumn.api.sign.sdk.internal.SdkDigestInputStream;
import com.autumn.api.sign.sdk.util.HttpUtils;
import com.autumn.api.sign.sdk.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.Map.Entry;

public abstract class AbstractSigner implements Signer {
  public AbstractSigner() {}

  public byte[] sign(String stringData, byte[] key, SigningAlgorithm algorithm)
      throws ClientException {
    try {
      byte[] data = stringData.getBytes(StringUtils.UTF8);
      return this.sign(data, key, algorithm);
    } catch (Exception var5) {
      throw new ClientException(
          "Unable to calculate a request signature: " + var5.getMessage(), var5);
    }
  }

  protected byte[] sign(byte[] data, byte[] key, SigningAlgorithm algorithm)
      throws ClientException {
    try {
      Mac mac = Mac.getInstance(algorithm.toString());
      mac.init(new SecretKeySpec(key, algorithm.toString()));
      return mac.doFinal(data);
    } catch (Exception var5) {
      throw new ClientException(
          "Unable to calculate a request signature: " + var5.getMessage(), var5);
    }
  }

  public byte[] hash(String text) throws ClientException {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(text.getBytes(StringUtils.UTF8));
      return md.digest();
    } catch (Exception var3) {
      throw new ClientException(
          "Unable to compute hash while signing request: " + var3.getMessage(), var3);
    }
  }

  protected byte[] hash(InputStream input) throws ClientException {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      DigestInputStream digestInputStream = new SdkDigestInputStream(input, md);
      byte[] buffer = new byte[1024];

      while (digestInputStream.read(buffer) > -1) {}

      return digestInputStream.getMessageDigest().digest();
    } catch (Exception var5) {
      throw new ClientException(
          "Unable to compute hash while signing request: " + var5.getMessage(), var5);
    }
  }

  public byte[] hash(byte[] data) throws ClientException {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(data);
      return md.digest();
    } catch (Exception var3) {
      throw new ClientException(
          "Unable to compute hash while signing request: " + var3.getMessage(), var3);
    }
  }

  protected String getCanonicalizedQueryString(Map<String, List<String>> parameters) {
    SortedMap<String, List<String>> sorted = new TreeMap();
    Iterator var3 = parameters.entrySet().iterator();

    while (var3.hasNext()) {
      Entry<String, List<String>> entry = (Entry) var3.next();
      String encodedParamName = HttpUtils.urlEncode((String) entry.getKey(), false);
      List<String> paramValues = (List) entry.getValue();
      List<String> encodedValues = new ArrayList(paramValues.size());
      Iterator var8 = paramValues.iterator();

      while (var8.hasNext()) {
        String value = (String) var8.next();
        encodedValues.add(HttpUtils.urlEncode(value, false));
      }

      Collections.sort(encodedValues);
      sorted.put(encodedParamName, encodedValues);
    }

    StringBuilder result = new StringBuilder();
    Iterator var11 = sorted.entrySet().iterator();

    while (var11.hasNext()) {
      Entry<String, List<String>> entry = (Entry) var11.next();

      String value;
      for (Iterator var13 = ((List) entry.getValue()).iterator();
          var13.hasNext();
          result.append((String) entry.getKey()).append("=").append(value)) {
        value = (String) var13.next();
        if (result.length() > 0) {
          result.append("&");
        }
      }
    }

    return result.toString();
  }

  protected String getCanonicalizedQueryString(Request<?> request) {
    return HttpUtils.usePayloadForQueryParameters(request)
        ? ""
        : this.getCanonicalizedQueryString(request.getParameters());
  }

  protected byte[] getBinaryRequestPayload(Request<?> request) {
    if (HttpUtils.usePayloadForQueryParameters(request)) {
      String encodedParameters = HttpUtils.encodeParameters(request);
      return encodedParameters == null ? new byte[0] : encodedParameters.getBytes(StringUtils.UTF8);
    } else {
      return this.getBinaryRequestPayloadWithoutQueryParams(request);
    }
  }

  protected String getRequestPayload(Request<?> request) {
    return this.newString(this.getBinaryRequestPayload(request));
  }

  protected String getRequestPayloadWithoutQueryParams(Request<?> request) {
    return this.newString(this.getBinaryRequestPayloadWithoutQueryParams(request));
  }

  protected byte[] getBinaryRequestPayloadWithoutQueryParams(Request<?> request) {
    InputStream content = this.getBinaryRequestPayloadStreamWithoutQueryParams(request);
    ByteArrayOutputStream byteArrayOutputStream = null;

    try {
      WebServiceRequest req = request.getOriginalRequest();
      content.mark(req == null ? -1 : req.getReadLimit());
      byteArrayOutputStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[5120];

      while (true) {
        int bytesRead = content.read(buffer);
        if (bytesRead == -1) {
          content.reset();
          byte[] var17 = byteArrayOutputStream.toByteArray();
          return var17;
        }

        byteArrayOutputStream.write(buffer, 0, bytesRead);
      }
    } catch (Exception var15) {
      throw new ClientException(
          "Unable to read request payload to sign request: " + var15.getMessage(), var15);
    } finally {
      if (byteArrayOutputStream != null) {
        try {
          byteArrayOutputStream.close();
        } catch (IOException var14) {
          throw new ClientException(
              "Unable to close byteArrayOutputStream: " + var14.getMessage(), var14);
        }
      }
    }
  }

  protected InputStream getBinaryRequestPayloadStream(Request<?> request) {
    if (HttpUtils.usePayloadForQueryParameters(request)) {
      String encodedParameters = HttpUtils.encodeParameters(request);
      return encodedParameters == null
          ? new ByteArrayInputStream(new byte[0])
          : new ByteArrayInputStream(encodedParameters.getBytes(StringUtils.UTF8));
    } else {
      return this.getBinaryRequestPayloadStreamWithoutQueryParams(request);
    }
  }

  protected InputStream getBinaryRequestPayloadStreamWithoutQueryParams(Request<?> request) {
    try {
      InputStream is = request.getContent();
      if (is == null) {
        return new ByteArrayInputStream(new byte[0]);
      } else if (!is.markSupported()) {
        throw new ClientException("Unable to read request payload to sign request.");
      } else {
        return is;
      }
    } catch (Exception var3) {
      throw new ClientException(
          "Unable to read request payload to sign request: " + var3.getMessage(), var3);
    }
  }

  protected String getCanonicalizedResourcePath(String resourcePath) {
    return this.getCanonicalizedResourcePath(resourcePath, true);
  }

  protected String getCanonicalizedResourcePath(String resourcePath, boolean urlEncode) {
    if (resourcePath != null && !resourcePath.isEmpty()) {
      String value = urlEncode ? HttpUtils.urlEncode(resourcePath, true) : resourcePath;
      return value.startsWith("/") ? value : "/".concat(value);
    } else {
      return "/";
    }
  }

  protected String getCanonicalizedEndpoint(URI endpoint) {
    String endpointForStringToSign = endpoint.getHost().toLowerCase();
    if (HttpUtils.isUsingNonDefaultPort(endpoint)) {
      endpointForStringToSign = endpointForStringToSign + ":" + endpoint.getPort();
    }

    return endpointForStringToSign;
  }

  protected Credentials sanitizeCredentials(Credentials credentials) {
    String accessKeyId = credentials.getAccessKeyId();
    String secretKey = credentials.getSecretKey();
    if (secretKey != null) {
      secretKey = secretKey.trim();
    }

    if (accessKeyId != null) {
      accessKeyId = accessKeyId.trim();
    }

    return new BasicCredentials(accessKeyId, secretKey);
  }

  protected String newString(byte[] bytes) {
    return new String(bytes, StringUtils.UTF8);
  }

  protected Date getSignatureDate(int offsetInSeconds) {
    return new Date(System.currentTimeMillis() - (long) (offsetInSeconds * 1000));
  }
}
