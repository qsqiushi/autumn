//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autumn.api.sign.sdk.util;

import com.autumn.api.sign.sdk.Request;
import com.autumn.api.sign.sdk.http.HttpMethodName;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils {
  private static final String DEFAULT_ENCODING = "UTF-8";
  private static final Pattern ENCODED_CHARACTERS_PATTERN;

  static {
    StringBuilder pattern = new StringBuilder();
    pattern
        .append(Pattern.quote("+"))
        .append("|")
        .append(Pattern.quote("*"))
        .append("|")
        .append(Pattern.quote("%7E"))
        .append("|")
        .append(Pattern.quote("%2F"));
    ENCODED_CHARACTERS_PATTERN = Pattern.compile(pattern.toString());
  }

  public HttpUtils() {}

  public static String urlEncode(String value, boolean path) {
    if (value == null) {
      return "";
    } else {
      try {
        String encoded = URLEncoder.encode(value, "UTF-8");
        Matcher matcher = ENCODED_CHARACTERS_PATTERN.matcher(encoded);

        StringBuffer buffer;
        String replacement;
        for (buffer = new StringBuffer(encoded.length());
            matcher.find();
            matcher.appendReplacement(buffer, replacement)) {
          replacement = matcher.group(0);
          if ("+".equals(replacement)) {
            replacement = "%20";
          } else if ("*".equals(replacement)) {
            replacement = "%2A";
          } else if ("%7E".equals(replacement)) {
            replacement = "~";
          } else if (path && "%2F".equals(replacement)) {
            replacement = "/";
          }
        }

        matcher.appendTail(buffer);
        return buffer.toString();
      } catch (UnsupportedEncodingException var6) {
        throw new RuntimeException(var6);
      }
    }
  }

  public static boolean isUsingNonDefaultPort(URI uri) {
    String scheme = uri.getScheme().toLowerCase();
    int port = uri.getPort();
    if (port <= 0) {
      return false;
    } else if (scheme.equals("http") && port == 80) {
      return false;
    } else {
      return !scheme.equals("https") || port != 443;
    }
  }

  public static boolean usePayloadForQueryParameters(Request<?> request) {
    boolean requestIsPOST = HttpMethodName.POST.equals(request.getHttpMethod());
    boolean requestHasNoPayload = request.getContent() == null;
    return requestIsPOST && requestHasNoPayload;
  }

  public static String encodeParameters(Request<?> request) {
    List<NameValuePair> nameValuePairs = null;
    int size = request.getParameters().size();
    if (size > 0) {
      nameValuePairs = new ArrayList(size);
      List parameters = new ArrayList(request.getParameters().entrySet());

      Collections.sort(
          parameters,
          new Comparator() {
            @Override
            public int compare(Object arg1, Object arg2) {
              Entry<String, String> obj1 = (Entry) arg1;
              Entry<String, String> obj2 = (Entry) arg2;
              return ((String) obj1.getKey()).toString().compareTo((String) obj2.getKey());
            }
          });
      Iterator iterator = parameters.iterator();

      while (iterator.hasNext()) {
        Entry<String, String> parameter = (Entry) iterator.next();
        nameValuePairs.add(
            new BasicNameValuePair((String) parameter.getKey(), (String) parameter.getValue()));
      }
    }

    String encodedParams = null;
    if (nameValuePairs != null) {
      encodedParams = URLEncodedUtils.format(nameValuePairs, "UTF-8");
    }

    return encodedParams;
  }

  public static String appendUri(String baseUri, String path) {
    return appendUri(baseUri, path, false);
  }

  public static String appendUri(String baseUri, String path, boolean escapeDoubleSlash) {
    String resultUri = baseUri;
    if (path != null && path.length() > 0) {
      if (path.startsWith("/")) {
        if (baseUri.endsWith("/")) {
          resultUri = baseUri.substring(0, baseUri.length() - 1);
        }
      } else if (!baseUri.endsWith("/")) {
        resultUri = baseUri + "/";
      }

      String encodedPath = urlEncode(path, true);
      if (escapeDoubleSlash) {
        encodedPath = encodedPath.replace("//", "/%2F");
      }

      resultUri = resultUri + encodedPath;
    } else if (!baseUri.endsWith("/")) {
      resultUri = baseUri + "/";
    }

    return resultUri;
  }
}
