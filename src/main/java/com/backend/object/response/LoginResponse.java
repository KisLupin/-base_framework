package com.backend.object.response;

import lombok.Data;

@Data
public class LoginResponse {
    private Object profile;
    private String token;
}
