package com.backend.object.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortRequest {
    private String variable;

    @ApiModelProperty(notes = "Values in ASC, DESC")
    private String sort = "ASC";

    public SortRequest(String variable, String sort) {
        this.variable = variable;
        this.sort = sort;
    }
}
