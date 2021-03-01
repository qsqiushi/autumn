
package com.autu.api.sign.sdk.http;

import com.autu.api.sign.sdk.Request;
import com.autu.api.sign.sdk.Response;

public interface RequestHandler {
    void beforeRequest(Request<?> var1);

    void afterResponse(Request<?> var1, Response<?> var2);

    void afterError(Request<?> var1, Response<?> var2, Exception var3);
}
