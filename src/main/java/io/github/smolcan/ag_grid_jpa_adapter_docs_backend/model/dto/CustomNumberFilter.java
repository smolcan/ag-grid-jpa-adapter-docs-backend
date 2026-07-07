package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto;

import io.github.smolcan.aggrid.jpa.adapter.filter.IFilter;
import jakarta.persistence.criteria.*;
import lombok.NonNull;

import java.util.Map;

public class CustomNumberFilter<T extends Number> extends IFilter<T, CustomNumberFilterModel, CustomNumberFilterParams> {

    @Override
    // map to filter model object
    public CustomNumberFilterModel recognizeFilterModel(Map<String, Object> map) {
        CustomNumberFilterModel customNumberFilter = new CustomNumberFilterModel();
        customNumberFilter.setValue(map.get("value").toString());
        return customNumberFilter;
    }

    @Override
    @NonNull
    // default params with default values
    public CustomNumberFilterParams getDefaultFilterParams() {
        return CustomNumberFilterParams.builder().build();
    }

    @Override
    @NonNull
    // create predicate from expression
    public Predicate toPredicate(@NonNull CriteriaBuilder cb, @NonNull Expression<T> expression, @NonNull CustomNumberFilterModel customNumberFilterModel) {
        String value = customNumberFilterModel.getValue();
        if (value == null || value.equalsIgnoreCase("All")) {
            // not active, always true
            return cb.and();
        }

        Expression<Integer> integerExpression = expression.as(Integer.class);
        Predicate predicate;
        if (value.equalsIgnoreCase("Even")) {
            predicate = cb.equal(cb.mod(integerExpression, 2), 0);
        } else {
            predicate = cb.notEqual(cb.mod(integerExpression, 2), 0);
        }

        // predicate also depends on filter params
        if (this.filterParams.isIncludeNullValues()) {
            predicate = cb.or(predicate, cb.isNull(expression));
        }

        return predicate;
    }
}
