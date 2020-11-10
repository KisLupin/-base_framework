package com.backend.service.impl;

import com.backend.cache.CacheRepository;
import com.backend.cache.UserCache;
import com.backend.domain.UserResponse;
import com.backend.enumeration.ErrorCode;
import com.backend.enumeration.FilterOperator;
import com.backend.enumeration.SQLDataType;
import com.backend.exception.RestApiException;
import com.backend.object.filter.UserFilters;
import com.backend.object.request.FilterRequest;
import com.backend.object.request.LoginRequest;
import com.backend.object.request.SortRequest;
import com.backend.object.request.UserRequest;
import com.backend.object.response.LoginResponse;
import com.backend.repository.CustomRepository;
import com.backend.repository.UserRepository;
import com.backend.security.SecurityUtils;
import com.backend.security.TokenProvider;
import com.backend.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements BaseService<UserResponse, UserRequest, UserFilters> {
    private final UserRepository userRepository;
    private final CustomRepository customRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final CacheRepository<UserCache> userCacheRepository;

    @Override
    public UserResponse create(UserRequest condition) {
        condition.setPassword(passwordEncoder.encode(condition.getPassword()));
        return userRepository.save(new UserResponse(condition));
    }

    @Override
    public UserResponse edit(UserRequest condition) {
        UserResponse userResponse = userRepository.findByUsername(condition.getUsername())
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_EXIST));
        userResponse.update(condition);
        return userRepository.save(userResponse);
    }

    @Override
    public UserResponse delete(Integer id) {
        return null;
    }

    @Override
    public UserResponse clear(Integer id) {
        return null;
    }

    @Override
    public UserResponse restore(Integer id) {
        return null;
    }

    @Override
    public Page<UserResponse> filter(UserFilters filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        List<FilterRequest> filters = filter.getFilterRequests();
        if (!StringUtils.isEmpty(filter.getUsername())){
            FilterRequest filterRequest = new FilterRequest(
                    "username",
                    FilterOperator.EQUAL.name(),
                    filter.getUsername(),
                    SQLDataType.TEXT.name()
            );
            filters.add(filterRequest);
        }
        if (filter.getSorts().size() == 0){
            filter.getSorts().add(new SortRequest("id", "DESC"));
        }
        return customRepository.findByFilterAndSortAndPage(UserResponse.class, filters, filter.getSorts(), pageable);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        UserResponse userResponse = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RestApiException(ErrorCode.USERNAME_NOT_EXIST));
        if (!passwordEncoder.matches(loginRequest.getPassword(), userResponse.getPassword())) {
            throw new RestApiException(ErrorCode.PASSWORD_INVALID);
        }
        // save last login
        userRepository.save(userResponse);
        String token = tokenProvider.createToken(userResponse, loginRequest.getRememberMe());
        userCacheRepository
                .add(new UserCache(token, userResponse.getId(), userResponse.getUsername(), Duration.ofSeconds(86400)));
        LoginResponse response = new LoginResponse();
        response.setProfile(userResponse);
        response.setToken(token);
        return response;
    }

    @Override
    public void logout() {
        userCacheRepository.delete(SecurityUtils.getCurrentUserTokenKey().get());
    }
}
