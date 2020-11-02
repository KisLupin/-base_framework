package com.backend.repository.impl;

import com.backend.common.Constant;
import com.backend.enumeration.FilterOperator;
import com.backend.enumeration.SQLDataType;
import com.backend.object.request.FilterRequest;
import com.backend.object.request.SortRequest;
import com.backend.repository.CustomRepository;
import com.backend.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@AllArgsConstructor
public class CustomRepositoryImpl implements CustomRepository {
    private final EntityManager entityManager;

    public <T> Page<T> findByFilterAndSortAndPage(Class<T> classType, List<FilterRequest> filters,
                                                  List<SortRequest> sorts, Pageable pageable) {
        Long totalRecords = countByFilter(classType, filters);
        List<T> data = new ArrayList<T>();
        if (totalRecords > 0) {
            data = selectByFilterAndSortAndPage(classType, filters, sorts, pageable);
        }
        return new PageImpl<>(data, pageable, totalRecords);
    }

    public <T> List<T> selectByFilterAndSortAndPage(Class<T> classType, List<FilterRequest> filters,
                                                    List<SortRequest> sorts, Pageable pageable) {
        String sql = selectSQL(classType, filters, sorts);
        return entityManager.createQuery(sql, classType)
                .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
                .setMaxResults(pageable.getPageSize()).getResultList();
    }

    public <T> Long countByFilter(Class<T> classType, List<FilterRequest> filters) {
        String sql = countSQL(classType, filters);
        return entityManager.createQuery(sql, Long.class).getSingleResult();
    }

    private <T> String selectSQL(Class<T> classType, List<FilterRequest> filters,
                                 List<SortRequest> sorts) {
        StringBuilder sqlBuilder = new StringBuilder().append("Select tb from ")
                .append(classType.getName()).append(" tb").append(" where 1 = 1");
        boolean firstCond = true;
        for (FilterRequest filter : filters) {
            String conditionSql = genCondition(filter);
            if (StringUtils.isEmpty(conditionSql)) {
                continue;
            }
            if (firstCond) {
                sqlBuilder.append(" and ").append(conditionSql);
                firstCond = false;
            } else {
                String combineStr = filter.isCombineWithOtherFilter() ? " or " : " and ";
                sqlBuilder.append(combineStr).append(conditionSql);
            }
        }
        if (Objects.nonNull(sorts)) {

            List<String> sortSqlList = new ArrayList<>();
            for (SortRequest sort : sorts) {
                String variable = sort.getVariable();
                if (StringUtils.isEmpty(variable)) {
                    continue;
                }
                String sortSql = sort.getVariable() + " " + sort.getSort();
                sortSqlList.add(sortSql);
            }
            if (!CollectionUtils.isEmpty(sortSqlList)) {
                sqlBuilder.append(" order by ").append(String.join(",", sortSqlList));
            }
        }
        return sqlBuilder.toString();
    }

    private <T> String countSQL(Class<T> classType, List<FilterRequest> filters) {
        // get
        StringBuilder sqlBuilder = new StringBuilder().append("Select count(tb.id) from ")
                .append(classType.getName()).append(" tb").append(" where 1 = 1");
        boolean firstCond = true;
        for (FilterRequest filter : filters) {
            String conditionSql = genCondition(filter);
            if (StringUtils.isEmpty(conditionSql)) {
                continue;
            }
            if (firstCond) {
                sqlBuilder.append(" and ").append(conditionSql);
                firstCond = false;
            } else {
                String combineStr = filter.isCombineWithOtherFilter() ? " or " : " and ";
                sqlBuilder.append(combineStr).append(conditionSql);
            }
        }
        return sqlBuilder.toString();
    }

    private String genCondition(FilterRequest filter) {
        try {
            String variables = filter.getVariable();
            String operator = filter.getOperator();
            if (StringUtils.isEmpty(variables) || StringUtils.isEmpty(operator)) {
                return null;
            }
            String value = convertValueByDataType(filter.getValue(), filter.getDataType());
            String[] variableList = variables.split(",");
            List<String> conditionList = new ArrayList<>();
            for (String variable : variableList) {
                StringBuilder conditionSql = new StringBuilder();
                if (FilterOperator.EQUAL.name().equalsIgnoreCase(operator)) {
                    conditionSql.append(variable).append(" = ").append(value);
                } else if (FilterOperator.NOT_EQUAL.name().equalsIgnoreCase(operator)) {
                    conditionSql.append(variable).append(" <> ").append(value);
                } else if (FilterOperator.LESS_THAN.name().equalsIgnoreCase(operator)) {
                    conditionSql.append(variable).append(" < ").append(value);
                } else if (FilterOperator.MORE_THAN.name().equalsIgnoreCase(operator)) {
                    conditionSql.append(variable).append(" > ").append(value);
                } else if (FilterOperator.LESS_OR_EQUAL.name().equalsIgnoreCase(operator)) {
                    conditionSql.append(variable).append(" <= ").append(value);
                } else if (FilterOperator.MORE_OR_EQUAL.name().equalsIgnoreCase(operator)) {
                    conditionSql.append(variable).append(" >= ").append(value);
                } else if (FilterOperator.LIKE.name().equalsIgnoreCase(operator)) {
                    conditionSql.append(variable).append(" like concat('%',").append(value).append(", '%') ");
                } else if (FilterOperator.NOT_LIKE.name().equalsIgnoreCase(operator)) {
                    conditionSql.append(variable).append(" not like concat('%',").append(value)
                            .append(", '%') ");
                } else if (FilterOperator.START_WITH.name().equalsIgnoreCase(operator)) {
                    conditionSql.append(variable).append(" like concat(").append(value).append(", '%') ");
                } else if (FilterOperator.END_WITH.name().equalsIgnoreCase(operator)) {
                    conditionSql.append(variable).append(" like concat('%', ").append(value).append(") ");
                } else if (FilterOperator.CONTAINS.name().equalsIgnoreCase(operator)) {
                    conditionSql.append(variable).append(" like concat('%',").append(value).append(", '%') ");
                } else if (FilterOperator.EQUAL_IGNORECASE.name().equalsIgnoreCase(operator)) {
                    conditionSql.append("lower(").append(variable).append(")").append(" = ")
                            .append(value.toLowerCase());
                } else if (FilterOperator.CONTAINS_IGNORECASE.name().equalsIgnoreCase(operator)) {
                    conditionSql.append("lower(").append(variable).append(")").append(" like concat('%',")
                            .append(Objects.requireNonNull(value).toLowerCase()).append(", '%') ");
                } else if (FilterOperator.IN.name().equalsIgnoreCase(operator)
                        && StringUtils.isEmpty(value)) {
                    conditionSql.append("1 != 1");
                } else if (FilterOperator.IN.name().equalsIgnoreCase(operator)
                        && !StringUtils.isEmpty(value)) {
                    conditionSql.append(variable).append(" in ").append(value);
                } else if (FilterOperator.NOT_IN.name().equalsIgnoreCase(operator)
                        && !StringUtils.isEmpty(value)) {
                    conditionSql.append(variable).append(" not in ").append(value);
                } else if (FilterOperator.IS_NULL.name().equalsIgnoreCase(operator)) {
                    conditionSql.append(variable).append(" is null ");
                } else if (FilterOperator.IS_NOT_NULL.name().equalsIgnoreCase(operator)) {
                    conditionSql.append(variable).append(" is not null ");
                } else if (FilterOperator.CONTAINS_IGNORECASE_ACCENT.name().equalsIgnoreCase(operator)) {
                    conditionSql.append("fn_remove_accents(").append(variable).append(")").append(" like '%")
                            .append(Utils.removeAccent(value)).append("%' ");
                }
                conditionList.add(conditionSql.toString());
            }
            if (CollectionUtils.isEmpty(conditionList)) {
                return null;
            }
            return "(" + String.join(" or ", conditionList) + ")";
        } catch (Exception e) {
            System.out.println("error");
        }
        return null;
    }

    private String convertValueByDataType(String value, String dataType) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        if (SQLDataType.TEXT.name().equalsIgnoreCase(dataType)) {
            return new StringBuilder().append("'").append(value).append("'").toString();
        }
        if (SQLDataType.STRING.name().equalsIgnoreCase(dataType)) {
            return new StringBuilder().append("'").append(value).append("'").toString();
        }
        if (SQLDataType.NUMBER.name().equalsIgnoreCase(dataType)) {
            return String.valueOf(Double.valueOf(value).longValue());
        }
        if (SQLDataType.DATETIME.name().equalsIgnoreCase(dataType)) {
            return new StringBuilder().append("STR_TO_DATE('").append(value).append("', '")
                    .append(Constant.DateTimeFormat.DD_MM_YYYY_HH_MM_SS).append("')").toString();
        }
        if (SQLDataType.DATE.name().equalsIgnoreCase(dataType)) {
            return new StringBuilder().append("STR_TO_DATE('").append(value).append("', '")
                    .append(Constant.DateTimeFormat.DD_MM_YYYY_MYSQL_FORMAT).append("')").toString();
        }
        if (SQLDataType.ARRAY.name().equalsIgnoreCase(dataType)) {
            return new StringBuilder().append("(").append(value).append(")").toString();
        }
        if (SQLDataType.QUERY.name().equalsIgnoreCase(dataType)) {
            return new StringBuilder().append("(").append(value).append(")").toString();
        }
        if (SQLDataType.ORIGIN.name().equalsIgnoreCase(dataType)) {
            return new StringBuilder(value).toString();
        }
        return null;
    }
}
