package com.backend.exception;

import com.backend.enumeration.ErrorCode;
import com.backend.object.ApiResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestApiException extends RuntimeException{
    private static final long serialVersionUID = -2056887461208273628L;

    private final String errorCode;
    private final String defaultMessage;
    private Object data;

    public RestApiException(Integer status, String errorCode) {
        this(status, errorCode, "");
    }

    public RestApiException(Integer status, Object result, String errorCode, String defaultMessage) {
        super();
        this.errorCode = errorCode;
        this.defaultMessage = defaultMessage;
        this.data = result;
    }

    public RestApiException(ApiResponse apiResponse) {
        super();
        this.errorCode = apiResponse.getErrorCode();
        this.defaultMessage = apiResponse.getMessage();
        this.data = apiResponse.getData();
    }

    public RestApiException(Integer status, String errorCode, String defaultMessage) {
        this.defaultMessage = defaultMessage;
        this.errorCode = errorCode;
    }

    public RestApiException(ErrorCode error) {
        this.defaultMessage = error.getMessage();
        this.errorCode = error.getErrorCode();
    }

    public RestApiException(ErrorCode error, String message) {
        this.defaultMessage = message;
        this.errorCode = error.getErrorCode();
    }

    public RestApiException(ErrorCode error, Object data) {
        this.data = data;
        this.defaultMessage = error.getMessage();
        this.errorCode = error.getErrorCode();
    }
}
