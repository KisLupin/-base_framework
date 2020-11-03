package com.backend.service.impl;

import com.backend.domain.User;
import com.backend.enumeration.ErrorCode;
import com.backend.enumeration.FilterOperator;
import com.backend.enumeration.SQLDataType;
import com.backend.exception.RestApiException;
import com.backend.object.filter.UserFilters;
import com.backend.object.request.FilterRequest;
import com.backend.object.request.SortRequest;
import com.backend.object.request.UserRequest;
import com.backend.repository.CustomRepository;
import com.backend.repository.UserRepository;
import com.backend.service.BaseService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements BaseService<User, UserRequest, UserFilters> {
    private final UserRepository userRepository;
    private final CustomRepository customRepository;

    @Override
    public User create(UserRequest condition) {
        return userRepository.save(new User(condition));
    }

    @Override
    public User edit(UserRequest condition) {
        User user = userRepository.findByUsername(condition.getUsername())
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_EXIST));
        user.update(condition);
        return userRepository.save(user);
    }

    @Override
    public User delete(Integer id) {
        return null;
    }

    @Override
    public User clear(Integer id) {
        return null;
    }

    @Override
    public User restore(Integer id) {
        return null;
    }

    @Override
    public Page<User> filter(UserFilters filter) {
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
        return customRepository.findByFilterAndSortAndPage(User.class, filters, filter.getSorts(), pageable);
    }
}
