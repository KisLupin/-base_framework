package com.backend.object.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilters extends BaseRequest{
    private Integer id;
    private String username;
}
