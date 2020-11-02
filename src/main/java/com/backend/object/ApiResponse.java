package com.backend.object;

import com.backend.enumeration.ApiResponseStatus;
import com.backend.enumeration.ErrorCode;
import com.backend.exception.RestApiException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {
    private Integer status;

    private Object data;

    private String errorCode;

    private String message;

    public ApiResponse(Integer status, Object data, String errorCode, String message) {
        super();
        this.status = status;
        this.data = data;
        this.errorCode = errorCode;
        this.message = message;
    }

    public ApiResponse(Integer status, Object data) {
        super();
        this.status = status;
        this.data = data;
    }

    public ApiResponse(RestApiException ex) {
        super();
        this.status = ApiResponseStatus.FAILED.getValue();
        this.errorCode = ex.getErrorCode();
        this.message = ex.getDefaultMessage();
        this.data = ex.getData();
    }

    public ApiResponse(Exception ex, ErrorCode errorCode) {
        super();
        this.status = ApiResponseStatus.FAILED.getValue();
        this.errorCode = errorCode.getErrorCode();
        this.message = ex.getMessage();
    }

    public ApiResponse(Integer value) {
        super();
        this.status = value;
    }
}
