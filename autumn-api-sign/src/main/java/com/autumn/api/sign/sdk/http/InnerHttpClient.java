
package com.autumn.api.sign.sdk.http;


import com.autumn.api.sign.sdk.*;
import com.autumn.api.sign.sdk.annotation.ThreadSafe;
import com.autumn.api.sign.sdk.auth.credentials.Credentials;
import com.autumn.api.sign.sdk.auth.signer.Signer;
import com.autumn.api.sign.sdk.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@ThreadSafe
public class InnerHttpClient {
    private static final HttpRequestFactory httpRequestFactory = new HttpRequestFactory();
    private static final HttpClientFactory httpClientFactory = new HttpClientFactory();
    private final HttpClient httpClient;
    private final ClientConfiguration config;

    public InnerHttpClient(ClientConfiguration config) {
        this(config, httpClientFactory.createHttpClient(config));
    }

    InnerHttpClient(ClientConfiguration config, HttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
    }

    public <T> Response<T> execute(Request<?> request, HttpResponseHandler<WebServiceResponse<T>> responseHandler,
                                   ExecutionContext executionContext) {
        if (executionContext == null) {
            throw new ClientException("Internal SDK Error: No execution context parameter specified.");
        } else {
            List<RequestHandler> requestHandlers = this.requestHandler2s(request, executionContext);
            WebServiceRequest req = request.getOriginalRequest();
            Map<String, String> customHeaders = req.getCustomRequestHeaders();
            if (customHeaders != null) {
                request.getHeaders().putAll(customHeaders);
            }

            Response<T> response = null;
            InputStream origContent = request.getContent();

            Response var9;
            try {
                response = this.executeHelper(request, responseHandler, executionContext);
                this.afterResponse(request, requestHandlers, response);
                var9 = response;
            } catch (ClientException var13) {
                this.afterError(request, response, requestHandlers, var13);
                throw var13;
            } finally {
                IOUtils.closeQuietly(origContent);
            }

            return var9;
        }
    }

    private <T> void afterResponse(Request<?> request, List<RequestHandler> requestHandlers, Response<T> response) {
        Iterator var4 = requestHandlers.iterator();

        while (var4.hasNext()) {
            RequestHandler handler = (RequestHandler) var4.next();
            if (null != handler) {
                handler.afterResponse(request, response);
            }
        }

    }

    private void afterError(Request<?> request, Response<?> response, List<RequestHandler> requestHandlers,
                            ClientException e) {
        Iterator var5 = requestHandlers.iterator();

        while (var5.hasNext()) {
            RequestHandler handler = (RequestHandler) var5.next();
            if (null != handler) {
                handler.afterError(request, response, e);
            }
        }

    }

    private List<RequestHandler> requestHandler2s(Request<?> request, ExecutionContext executionContext) {
        List<RequestHandler> requestHandlers = executionContext.getRequestHandlers();
        if (null == requestHandlers) {
            return Collections.emptyList();
        } else {
            Iterator var4 = requestHandlers.iterator();

            while (var4.hasNext()) {
                RequestHandler requestHandler = (RequestHandler) var4.next();
                if (null != requestHandler) {
                    requestHandler.beforeRequest(request);
                }
            }

            return requestHandlers;
        }
    }

    private <T> Response<T> executeHelper(Request<?> request,
                                          HttpResponseHandler<WebServiceResponse<T>> responseHandler, ExecutionContext executionContext) {
        this.setUserAgent(request);
        ExecOneRequestParams p = new ExecOneRequestParams();

        try {
            return this.executeOneRequest(request, responseHandler, executionContext, p);
        } catch (IOException var6) {
            if (log.isInfoEnabled()) {
                log.info("Unable to execute HTTP request: " + var6.getMessage(), var6);
            }

            return null;
        }
    }

    private <T> Response<T> executeOneRequest(Request<?> request,
                                              HttpResponseHandler<WebServiceResponse<T>> responseHandler, ExecutionContext execContext,
                                              InnerHttpClient.ExecOneRequestParams p) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Sending Request: " + request);
        }

        Credentials credentials = execContext.getCredentials();
        p.newSigner(execContext);

        if ((p.signer != null) && (credentials != null)) {
            p.signer.sign(request, credentials);
        }

        p.newApacheRequest(httpRequestFactory, request, this.config, execContext);
        HttpContext httpContext = new BasicHttpContext();
        p.apacheResponse = this.httpClient.execute(p.apacheRequest, httpContext);
        HttpResponse httpResponse = this.createResponse(p.apacheRequest, request, p.apacheResponse);
        T response = this.handleResponse(responseHandler, httpResponse);
        return new Response(response, httpResponse);
    }

    private void setUserAgent(Request<?> request) {
        String userAgent = this.config.getUserAgent();
        if (userAgent != null) {
            request.addHeader("User-Agent", userAgent);
            if (!userAgent.equals("Apache HTTPClient")) {
                userAgent = userAgent + ", Apache HTTPClient";
            }

            WebServiceRequest req = request.getOriginalRequest();
            RequestClientOptions opts = req.getRequestClientOptions();
            if (opts != null) {
                String userAgentMarker = opts.getClientMarker(RequestClientOptions.Marker.USER_AGENT);
                if (userAgentMarker != null) {
                    request.addHeader("User-Agent", createUserAgentString(userAgent, userAgentMarker));
                }
            }

        }
    }

    private static String createUserAgentString(String existingUserAgentString, String userAgent) {
        return existingUserAgentString.contains(userAgent) ? existingUserAgentString
                : existingUserAgentString.trim() + " " + userAgent.trim();
    }

    public void shutdown() {
        this.httpClient.getConnectionManager().shutdown();
    }

    private <T> T handleResponse(HttpResponseHandler<WebServiceResponse<T>> responseHandler, HttpResponse httpResponse)
            throws IOException {
        try {
            WebServiceResponse<? extends T> response = (WebServiceResponse) responseHandler.handle(httpResponse);
            return response.getResult();
        } catch (IOException var5) {
            throw var5;
        } catch (ClientException var6) {
            throw var6;
        } catch (Exception var7) {
            String errorMessage = "Unable to unmarshall response (" + var7.getMessage() + "). Response Code: "
                    + httpResponse.getStatusCode() + ", Response Text: " + httpResponse.getStatusText();
            throw new ClientException(errorMessage, var7);
        }
    }

    private HttpResponse createResponse(HttpRequestBase apacheHttpRequest, Request<?> request,
                                        org.apache.http.HttpResponse apacheHttpResponse) throws IOException {
        HttpResponse httpResponse = new HttpResponse(request, apacheHttpRequest);
        if (apacheHttpResponse.getEntity() != null) {
            httpResponse.setContent(apacheHttpResponse.getEntity().getContent());
        }

        httpResponse.setStatusCode(apacheHttpResponse.getStatusLine().getStatusCode());
        httpResponse.setStatusText(apacheHttpResponse.getStatusLine().getReasonPhrase());
        Header[] var5 = apacheHttpResponse.getAllHeaders();
        int var6 = var5.length;

        for (int var7 = 0; var7 < var6; ++var7) {
            Header header = var5[var7];
            httpResponse.addHeader(header.getName(), header.getValue());
        }

        return httpResponse;
    }

    @Override
    protected void finalize() throws Throwable {
        this.shutdown();
        super.finalize();
    }

    static {
        List<String> problematicJvmVersions = Arrays.asList("1.6.0_06", "1.6.0_13", "1.6.0_17", "1.6.0_65", "1.7.0_45");
        String jvmVersion = System.getProperty("java.version");
        if (problematicJvmVersions.contains(jvmVersion)) {
            log.warn("Detected a possible problem with the current JVM version (" + jvmVersion
                    + ").  If you experience XML parsing problems using the SDK, try upgrading to a more recent JVM update.");
        }

    }

    private static class ExecOneRequestParams {
        private Signer signer;

        HttpRequestBase apacheRequest;

        org.apache.http.HttpResponse apacheResponse;

        Signer newSigner(ExecutionContext execContext) {
            this.signer = execContext.getSigner();
            return this.signer;
        }

        HttpRequestBase newApacheRequest(HttpRequestFactory httpRequestFactory, Request<?> request,
                                         ClientConfiguration config, ExecutionContext execContext) {
            this.apacheRequest = httpRequestFactory.createHttpRequest(request, config, execContext);
            return this.apacheRequest;
        }
    }
}
