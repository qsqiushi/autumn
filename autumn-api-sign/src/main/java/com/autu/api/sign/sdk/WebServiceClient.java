//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autu.api.sign.sdk;

import com.autu.api.sign.sdk.http.ExecutionContext;
import com.autu.api.sign.sdk.auth.signer.Signer;
import com.autu.api.sign.sdk.auth.signer.SignerFactory;
import com.autu.api.sign.sdk.http.InnerHttpClient;
import com.autu.api.sign.sdk.http.RequestHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class WebServiceClient {
    private static final String SDK = "SDK";
    private static final String S = "S";
    private final Object lock = new Object();
    protected volatile URI endpoint;
    private volatile String serviceName;
    private volatile String signerRegionOverride;
    protected ClientConfiguration clientConfiguration;
    protected InnerHttpClient client;
    protected final List<RequestHandler> requestHandlers;
    protected int timeOffset;
    private volatile Signer signer;

    public WebServiceClient(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
        this.client = new InnerHttpClient(clientConfiguration);
        this.requestHandlers = new CopyOnWriteArrayList();
    }

    public void setEndpoint(String endpoint) throws IllegalArgumentException {
        URI uri = this.toURI(endpoint);
        Signer signer = this.computeSigner(this.signerRegionOverride);
        synchronized (this.lock) {
            this.endpoint = uri;
            this.signer = signer;
        }
    }

    private URI toURI(String endpoint) throws IllegalArgumentException {
        if (!endpoint.contains("://")) {
            endpoint = this.clientConfiguration.getProtocol().toString() + "://" + endpoint;
        }

        try {
            return new URI(endpoint);
        } catch (URISyntaxException var3) {
            throw new IllegalArgumentException(var3);
        }
    }

    public Signer getSigner() {
        if (this.signer != null) {
            return this.signer;
        } else {
            Signer signer = this.computeSigner(this.signerRegionOverride);
            synchronized (this.lock) {
                this.signer = signer;
                return signer;
            }
        }
    }

    private Signer computeSigner(String signerRegionOverride) {
        String service = "";
        return this.computeSignerByServiceRegion(service, signerRegionOverride);
    }

    private Signer computeSignerByServiceRegion(String serviceName, String signerRegionOverride) {
        String signerType = this.clientConfiguration.getSignerOverride();
        Signer signer = signerType == null ? SignerFactory.getSigner(serviceName, signerRegionOverride)
            : SignerFactory.getSignerByTypeAndService(signerType, serviceName);
        return signer;
    }

    public void shutdown() {
        this.client.shutdown();
    }

    public void addRequestHandler(RequestHandler requestHandler) {
        this.requestHandlers.add(requestHandler);
    }

    public void removeRequestHandler(RequestHandler requestHandler) {
        this.requestHandlers.remove(requestHandler);
    }

    protected final ExecutionContext createExecutionContext() {
        return new ExecutionContext(this.requestHandlers, this);
    }

    public void setTimeOffset(int timeOffset) {
        this.timeOffset = timeOffset;
    }

    public WebServiceClient withTimeOffset(int timeOffset) {
        this.setTimeOffset(timeOffset);
        return this;
    }

    public int getTimeOffset() {
        return this.timeOffset;
    }

    public String getServiceName() {
        return "";
    }

    public final void setServiceNameIntern(String serviceName) {
        this.serviceName = serviceName;
    }

    public final String getSignerRegionOverride() {
        return this.signerRegionOverride;
    }

    public final void setSignerRegionOverride(String signerRegionOverride) {
        Signer signer = this.computeSigner(signerRegionOverride);
        synchronized (this.lock) {
            this.signer = signer;
            this.signerRegionOverride = signerRegionOverride;
        }
    }

    public <T extends WebServiceClient> T withEndpoint(String endpoint) {
        this.setEndpoint(endpoint);

        return (T)this;
    }
}
