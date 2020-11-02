package com.backend.enumeration;

public enum FilterOperator {
    EQUAL, EQUAL_IGNORECASE, NOT_EQUAL, LESS_THAN, MORE_THAN, LESS_OR_EQUAL, MORE_OR_EQUAL, LIKE, NOT_LIKE, IS_NULL, IS_NOT_NULL, IN, NOT_IN, START_WITH, END_WITH, CONTAINS, CONTAINS_IGNORECASE, CONTAINS_IGNORECASE_ACCENT;

    FilterOperator() {}

    private String value;

    public String getValue() {
        return value;
    }
}
