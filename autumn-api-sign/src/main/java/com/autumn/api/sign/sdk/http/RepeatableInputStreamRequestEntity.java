//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.autumn.api.sign.sdk.http;

import com.autumn.api.sign.sdk.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.InputStreamEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
class RepeatableInputStreamRequestEntity extends BasicHttpEntity {
    private boolean firstAttempt = true;
    private InputStreamEntity inputStreamRequestEntity;
    private InputStream content;
    private IOException originalException;

    RepeatableInputStreamRequestEntity(Request<?> request) {
        this.setChunked(false);
        long contentLength = -1L;

        String contentLengthString;
        try {
            contentLengthString = (String) request.getHeaders().get("Content-Length");
            if (contentLengthString != null) {
                contentLength = Long.parseLong(contentLengthString);
            }
        } catch (NumberFormatException var5) {
            log.warn("Unable to parse content length from request.  Buffering contents in memory.");
        }

        contentLengthString = (String) request.getHeaders().get("Content-Type");
        this.inputStreamRequestEntity = new InputStreamEntity(request.getContent(), contentLength);
        this.inputStreamRequestEntity.setContentType(contentLengthString);
        this.content = request.getContent();
        this.setContent(this.content);
        this.setContentType(contentLengthString);
        this.setContentLength(contentLength);
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public boolean isRepeatable() {
        return this.content.markSupported() || this.inputStreamRequestEntity.isRepeatable();
    }

    @Override
    public void writeTo(OutputStream output) throws IOException {
        try {
            if (!this.firstAttempt && this.isRepeatable()) {
                this.content.reset();
            }

            this.firstAttempt = false;
            this.inputStreamRequestEntity.writeTo(output);
        } catch (IOException var3) {
            if (this.originalException == null) {
                this.originalException = var3;
            }

            throw this.originalException;
        }
    }
}
