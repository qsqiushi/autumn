
package com.autumn.api.sign.sdk;

public class ServiceException extends ClientException {
    private static final long serialVersionUID = 1L;
    private String requestId;
    private String errorCode;
    private ServiceException.ErrorType errorType;
    private String errorMessage;
    private int statusCode;
    private String serviceName;

    public ServiceException(String errorMessage) {
        super((String)null);
        this.errorType = ServiceException.ErrorType.Unknown;
        this.errorMessage = errorMessage;
    }

    public ServiceException(String errorMessage, Exception cause) {
        super((String)null, cause);
        this.errorType = ServiceException.ErrorType.Unknown;
        this.errorMessage = errorMessage;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorType(ServiceException.ErrorType errorType) {
        this.errorType = errorType;
    }

    public ServiceException.ErrorType getErrorType() {
        return this.errorType;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getMessage() {
        return this.getErrorMessage() + " (Service: " + this.getServiceName() + "; Status Code: " + this.getStatusCode()
            + "; Error Code: " + this.getErrorCode() + "; Request ID: " + this.getRequestId() + ")";
    }

    public static enum ErrorType {
        Client, Service, Unknown;

        private ErrorType() {}
    }
}
