package com.backend.enumeration;

public enum ApiResponseStatus {
    SUCCESS(0), FAILED(1);

    private Integer value;

    ApiResponseStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
