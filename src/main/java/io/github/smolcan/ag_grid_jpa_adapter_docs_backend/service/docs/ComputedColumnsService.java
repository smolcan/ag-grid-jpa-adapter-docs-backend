package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;


import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade_;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.column.ComputedField;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.AgSetColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgNumberColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgTextColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
public class ComputedColumnsService {

    private final QueryBuilder<Trade, Long, Void> queryBuilder;

    @Autowired
    public ComputedColumnsService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        ColDef.builder(Trade_.currentValue)
                                .filter(new AgNumberColumnFilter<>())
                                .enableValue(true)
                                .build(),
                        ColDef.builder(Trade_.previousValue)
                                .filter(new AgNumberColumnFilter<>())
                                .enableValue(true)
                                .build(),
                        // computed: arithmetic over two columns, filterable and aggregatable
                        ColDef.builder(
                                        ComputedField.<Trade, BigDecimal>builder()
                                                .name("valueChange")
                                                .javaType(BigDecimal.class)
                                                .expressionFunction((cb, root) -> cb.diff(root.get(Trade_.currentValue), root.get(Trade_.previousValue)))
                                                .build()
                                )
                                .filter(new AgNumberColumnFilter<>())
                                .enableValue(true)
                                .build(),
                        // computed: CASE expression, grouped and filtered like any other column
                        ColDef.builder(
                                ComputedField.<Trade, String>builder()
                                        .name("valueBand")
                                        .javaType(String.class)
                                        .expressionFunction((cb, root) -> cb.<String>selectCase()
                                                .when(cb.greaterThan(root.get(Trade_.currentValue), root.get(Trade_.previousValue)), "UP")
                                                .when(cb.lessThan(root.get(Trade_.currentValue), root.get(Trade_.previousValue)), "DOWN")
                                                .otherwise("FLAT"))
                                        .build()
                                )
                                .filter(AgSetColumnFilter.forString())
                                .enableRowGroup(true, key -> key)
                                .build(),
                        // computed: string expression built from two columns
                        ColDef.builder(
                                        ComputedField.<Trade, String>builder()
                                                .name("portfolioBook")
                                                .javaType(String.class)
                                                .expressionFunction((cb, root) -> cb.concat(cb.concat(root.get(Trade_.portfolio), " / "), root.get(Trade_.book)))
                                                .build()
                                )
                                .filter(new AgTextColumnFilter())
                                .enableRowGroup(true, key -> key)
                                .build()
                )
                .build();
    }


    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        return this.queryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public List<Object> supplySetFilterValues(String field) {
        return this.queryBuilder.supplySetFilterValues(field);
    }

}
