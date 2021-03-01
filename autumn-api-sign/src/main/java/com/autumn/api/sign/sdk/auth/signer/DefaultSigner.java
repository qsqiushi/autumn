package com.autumn.api.sign.sdk.auth.signer;

import com.autumn.api.sign.sdk.ClientException;
import com.autumn.api.sign.sdk.auth.signer.internal.SignerRequestParams;
import com.autumn.api.sign.sdk.auth.signer.internal.SignerUtils;
import com.autumn.api.sign.sdk.util.BinaryUtils;
import com.autumn.api.sign.sdk.util.StringUtils;
import com.autumn.api.sign.sdk.Request;
import com.autumn.api.sign.sdk.WebServiceRequest;
import com.autumn.api.sign.sdk.auth.credentials.Credentials;
import com.autumn.api.sign.sdk.util.HttpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class DefaultSigner extends AbstractSigner implements Presigner, VerifySigner {
    private static final int SIGNER_CACHE_MAX_SIZE = 300;
    private static final String LINUX_NEW_LINE = "\n";
    protected boolean doubleUrlEncode;

    public DefaultSigner() {
        this(true);
    }

    public DefaultSigner(boolean doubleUrlEncoding) {
        this.doubleUrlEncode = doubleUrlEncoding;
    }

    @Override
    public void sign(Request<?> request, Credentials credentials) {
        Credentials sanitizedCredentials = this.sanitizeCredentials(credentials);
        String singerDate = this.getHeader(request, "X-Sdk-Date");
        SignerRequestParams signerParams = new SignerRequestParams(request, "SDK-HMAC-SHA256", singerDate);
        if (singerDate == null) {
            request.addHeader("X-Sdk-Date", signerParams.getFormattedSigningDateTime());
        }

        this.addHostHeader(request);
        String contentSha256 = this.calculateContentHash(request);
        String canonicalRequest = this.createCanonicalRequest(request, contentSha256);
        String stringToSign = this.createStringToSign(canonicalRequest, signerParams);
        byte[] signingKey = this.deriveSigningKey(sanitizedCredentials);
        byte[] signature = this.computeSignature(stringToSign, signingKey, signerParams);
        request.addHeader("Authorization",
            this.buildAuthorizationHeader(request, signature, sanitizedCredentials, signerParams));
    }

    @Override
    public void presignRequest(Request<?> request, Credentials credentials, Date userSpecifiedExpirationDate) {
        long expirationInSeconds = this.generateExpirationDate(userSpecifiedExpirationDate);
        this.addHostHeader(request);
        Credentials sanitizedCredentials = this.sanitizeCredentials(credentials);
        SignerRequestParams signerRequestParams = new SignerRequestParams(request, "SDK-HMAC-SHA256");
        String timeStamp = SignerUtils.formatTimestamp(System.currentTimeMillis());
        this.addPreSignInformationToRequest(request, sanitizedCredentials, signerRequestParams, timeStamp,
            expirationInSeconds);
        String contentSha256 = this.calculateContentHashPresign(request);
        String canonicalRequest = this.createCanonicalRequest(request, contentSha256);
        String stringToSign = this.createStringToSign(canonicalRequest, signerRequestParams);
        byte[] signingKey = this.deriveSigningKey(sanitizedCredentials);
        byte[] signature = this.computeSignature(stringToSign, signingKey, signerRequestParams);
        request.addParameter("X-Sdk-Signature", BinaryUtils.toHex(signature));
    }

    protected String createCanonicalRequest(Request<?> request, String contentSha256) {
        String path = HttpUtils.appendUri(request.getEndpoint().getPath(), request.getResourcePath());
        StringBuilder canonicalRequestBuilder = new StringBuilder(request.getHttpMethod().toString());
        canonicalRequestBuilder.append("\n").append(this.getCanonicalizedResourcePath(path, this.doubleUrlEncode))
            .append("\n").append(this.getCanonicalizedQueryString(request)).append("\n")
            .append(this.getCanonicalizedHeaderString(request)).append("\n")
            .append(this.getSignedHeadersString(request)).append("\n").append(contentSha256);
        String canonicalRequest = canonicalRequestBuilder.toString();
        return canonicalRequest;
    }

    protected String createStringToSign(String canonicalRequest, SignerRequestParams signerParams) {
        StringBuilder stringToSignBuilder = new StringBuilder(signerParams.getSigningAlgorithm());
        stringToSignBuilder.append("\n").append(signerParams.getFormattedSigningDateTime()).append("\n")
            .append(BinaryUtils.toHex(this.hash(canonicalRequest)));
        String stringToSign = stringToSignBuilder.toString();
        return stringToSign;
    }

    protected final byte[] deriveSigningKey(Credentials credentials) {
        return this.newSigningKey(credentials);
    }

    protected final byte[] computeSignature(String stringToSign, byte[] signingKey,
        SignerRequestParams signerRequestParams) {
        return this.sign(stringToSign.getBytes(StringUtils.UTF8), signingKey, SigningAlgorithm.HmacSHA256);
    }

    private String buildAuthorizationHeader(Request<?> request, byte[] signature, Credentials credentials,
                                            SignerRequestParams signerParams) {
        String credential = "Access=" + credentials.getAccessKeyId();
        String signerHeaders = "SignedHeaders=" + this.getSignedHeadersString(request);
        String signatureHeader = "Signature=" + BinaryUtils.toHex(signature);
        StringBuilder authHeaderBuilder = new StringBuilder();
        authHeaderBuilder.append("SDK-HMAC-SHA256").append(" ").append(credential).append(", ").append(signerHeaders)
            .append(", ").append(signatureHeader);
        return authHeaderBuilder.toString();
    }

    private void addPreSignInformationToRequest(Request<?> request, Credentials credentials,
                                                SignerRequestParams signerParams, String timeStamp, long expirationInSeconds) {
        String signingCredentials = credentials.getAccessKeyId();
        request.addParameter("X-Sdk-Algorithm", "SDK-HMAC-SHA256");
        request.addParameter("X-Sdk-Date", timeStamp);
        request.addParameter("X-Sdk-SignedHeaders", this.getSignedHeadersString(request));
        request.addParameter("X-Sdk-Expires", Long.toString(expirationInSeconds));
        request.addParameter("X-Sdk-Credential", signingCredentials);
    }

    protected String getCanonicalizedHeaderString(Request<?> request) {
        List<String> sortedHeaders = new ArrayList(request.getHeaders().keySet());
        Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);
        Map<String, String> requestHeaders = request.getHeaders();
        StringBuilder buffer = new StringBuilder();

        for (Iterator var5 = sortedHeaders.iterator(); var5.hasNext(); buffer.append("\n")) {
            String header = (String)var5.next();
            String key = header.toLowerCase();
            String value = (String)requestHeaders.get(header);
            buffer.append(key).append(":");
            if (value != null) {
                buffer.append(value.trim());
            }
        }

        return buffer.toString();
    }

    protected String getSignedHeadersString(Request<?> request) {
        List<String> sortedHeaders = new ArrayList(request.getHeaders().keySet());
        Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);
        StringBuilder buffer = new StringBuilder();

        String header;
        for (Iterator var4 = sortedHeaders.iterator(); var4.hasNext(); buffer.append(header.toLowerCase())) {
            header = (String)var4.next();
            if (buffer.length() > 0) {
                buffer.append(";");
            }
        }

        return buffer.toString();
    }

    protected void addHostHeader(Request<?> request) {
        boolean haveHostHeader = false;
        Iterator var3 = request.getHeaders().keySet().iterator();

        while (var3.hasNext()) {
            String key = (String)var3.next();
            if ("Host".equalsIgnoreCase(key)) {
                haveHostHeader = true;
                break;
            }
        }

        if (!haveHostHeader) {
            URI endpoint = request.getEndpoint();
            StringBuilder hostHeaderBuilder = new StringBuilder(endpoint.getHost());
            if (HttpUtils.isUsingNonDefaultPort(endpoint)) {
                hostHeaderBuilder.append(":").append(endpoint.getPort());
            }

            request.addHeader("Host", hostHeaderBuilder.toString());
        }

    }

    protected String getHeader(Request<?> request, String header) {
        if (header == null) {
            return null;
        } else {
            Map<String, String> headers = request.getHeaders();
            Iterator var4 = headers.keySet().iterator();

            String key;
            do {
                if (!var4.hasNext()) {
                    return null;
                }

                key = (String)var4.next();
            } while (!header.equalsIgnoreCase(key));

            return (String)headers.get(key);
        }
    }

    protected String calculateContentHash(Request<?> request) {
        String content_sha256 = this.getHeader(request, "x-sdk-content-sha256");
        if (content_sha256 != null) {
            return content_sha256;
        } else {
            InputStream payloadStream = this.getBinaryRequestPayloadStream(request);
            WebServiceRequest req = request.getOriginalRequest();
            payloadStream.mark(req == null ? -1 : req.getReadLimit());
            String contentSha256 = BinaryUtils.toHex(this.hash(payloadStream));

            try {
                payloadStream.reset();
                return contentSha256;
            } catch (IOException var7) {
                throw new ClientException("Unable to reset stream after calculating signature", (Throwable)null);
            }
        }
    }

    protected String calculateContentHashPresign(Request<?> request) {
        return this.calculateContentHash(request);
    }

    private long generateExpirationDate(Date expirationDate) {
        long expirationInSeconds =
            expirationDate != null ? (expirationDate.getTime() - System.currentTimeMillis()) / 1000L : 604800L;
        if (expirationDate != null && expirationInSeconds > 604800L) {
            throw new ClientException(
                "Requests that are pre-signed by SigV4 algorithm are valid for at most 7 days. The expiration date set on the current request ["
                    + SignerUtils.formatTimestamp(expirationDate.getTime()) + "] has exceeded this limit.");
        } else {
            return expirationInSeconds;
        }
    }

    private byte[] newSigningKey(Credentials credentials) {
        return credentials.getSecretKey().getBytes(StringUtils.UTF8);
    }

    public boolean verify(Request<?> request, Credentials credentials) {
        Credentials sanitizedCredentials = this.sanitizeCredentials(credentials);
        String singerDate = (String)request.getHeaders().get("X-Sdk-Date".toLowerCase());
        String authorization = (String)request.getHeaders().remove("Authorization".toLowerCase());
        SignerRequestParams signerParams = new SignerRequestParams(request, "SDK-HMAC-SHA256", singerDate);
        String contentSha256 = this.calculateContentHash(request);
        String canonicalRequest = this.createCanonicalRequest(request, contentSha256);
        String stringToSign = this.createStringToSign(canonicalRequest, signerParams);
        byte[] signingKey = this.deriveSigningKey(sanitizedCredentials);
        byte[] signature = this.computeSignature(stringToSign, signingKey, signerParams);
        String signatureResult = this.buildAuthorizationHeader(request, signature, sanitizedCredentials, signerParams);
        return signatureResult.equals(authorization);
    }

}
