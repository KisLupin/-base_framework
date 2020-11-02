package com.backend.enumeration;

public enum ErrorCode {
    API_FAILED_UNKNOWN("error.api.failed.unknown", ""),
    USER_NOT_EXIST("error.api.user_not_exist", ""),
    TIME_FORMAT_INVALID("error.api.time_format_invalid", ""),
    TOKEN_NOT_EXIST("error.api.token_not_exist", "");

    private String errorCode;
    private String message;

    ErrorCode(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
