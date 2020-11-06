package com.backend.controller;

import com.backend.domain.User;
import com.backend.enumeration.ApiResponseStatus;
import com.backend.enumeration.ErrorCode;
import com.backend.exception.RestApiException;
import com.backend.object.ApiResponse;
import com.backend.object.filter.UserFilters;
import com.backend.object.request.UserRequest;
import com.backend.service.BaseService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class Controller {
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
    private final BaseService<User, UserRequest, UserFilters> userService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> add(@RequestBody UserRequest req) {
        ApiResponse response;
        try {
            User user = userService.create(req);
            response = new ApiResponse(ApiResponseStatus.SUCCESS.getValue(), user);
        } catch (RestApiException ex) {
            response = new ApiResponse(ex);
            LOGGER.error("Error Occur, ", ex);
        } catch (Exception e) {
            response = new ApiResponse(e, ErrorCode.API_FAILED_UNKNOWN);
            LOGGER.error("Error Occur, ", e);
        }
        return ResponseEntity.ok().body(response);
    }
}
