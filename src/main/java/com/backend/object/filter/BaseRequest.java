package com.backend.object.filter;

import com.backend.object.request.FilterRequest;
import com.backend.object.request.SortRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BaseRequest {
    int page = 0;
    int size = 20;
    List<FilterRequest> filterRequests = new ArrayList<>();
    List<SortRequest> sorts = new ArrayList<>();
}
