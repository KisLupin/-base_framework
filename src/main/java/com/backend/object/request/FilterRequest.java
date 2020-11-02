package com.backend.object.request;

import com.backend.enumeration.FilterOperator;
import com.backend.enumeration.SQLDataType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterRequest {
    private String variable;

    @ApiModelProperty(notes = "Values in EQUAL, " + "NOT_EQUAL, LESS_THAN, MORE_THAN"
            + ", LESS_OR_EQUAL, MORE_OR_EQUAL, LIKE, NOT_LIKE, START_WITH," + " END_WITH, CONTAINS")
    private String operator = FilterOperator.EQUAL.name();

    private String value;

    @ApiModelProperty(notes = "Values in TEXT, NUMBER, DATETIME")
    private String dataType = SQLDataType.TEXT.name();

    // mặc định là phép and với các filter khác
    private boolean combineWithOtherFilter;

    public FilterRequest() {
        super();
    }

    public FilterRequest(String variable, String operator, String value, String dataType) {
        super();
        this.variable = variable;
        this.operator = operator;
        this.value = value;
        this.dataType = dataType;
    }

    public FilterRequest(String variable, String operator, String value, String dataType,
                         boolean combineWithOtherFilter) {
        super();
        this.variable = variable;
        this.operator = operator;
        this.value = value;
        this.dataType = dataType;
        this.combineWithOtherFilter = combineWithOtherFilter;
    }
}
