package com.backend.repository;

import com.backend.object.request.FilterRequest;
import com.backend.object.request.SortRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomRepository {
    <T> List<T> selectByFilterAndSortAndPage(Class<T> classType, List<FilterRequest> filters,
                                             List<SortRequest> sorts, Pageable pageable);

    <T> Page<T> findByFilterAndSortAndPage(Class<T> classType, List<FilterRequest> filters,
                                           List<SortRequest> sorts, Pageable pageable);
}
