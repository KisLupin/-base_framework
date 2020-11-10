package com.backend.service;

import com.backend.object.request.LoginRequest;
import com.backend.object.response.LoginResponse;
import org.springframework.data.domain.Page;

public interface BaseService<T,K,V> {
    T create(K condition);
    T edit(K condition);
    T delete(Integer id);
    T clear(Integer id);
    T restore(Integer id);
    Page<T> filter(V filter);
    LoginResponse login(LoginRequest loginRequest);
    void logout();
}
