package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Submitter_;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.SubmitterDeal_;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade_;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.column.FieldPath;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgDateColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgNumberColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AggregationService {

    private final QueryBuilder<Trade, Long, Void> queryBuilder;
    private final QueryBuilder<Trade, Long, Void> queryBuilderSuppressAggFilteredOnly;
    private final QueryBuilder<Trade, Long, Void> queryBuilderGroupAggFiltering;
    private final QueryBuilder<Trade, Long, Void> queryBuilderCustomAggregation;

    @Autowired
    public AggregationService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.product)
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder(Trade_.book)
                                .enableRowGroup(true)
                                .build(),
                        // numbers
                        ColDef.builder(FieldPath.of(Trade_.submitter).to(Submitter_.id))
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        ColDef.builder(FieldPath.of(Trade_.submitterDeal).to(SubmitterDeal_.id))
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        // date
                        ColDef.builder(Trade_.birthDate)
                                .enableValue(true)
                                .filter(AgDateColumnFilter.forLocalDate())
                                .build()
                )
                .build();

        this.queryBuilderCustomAggregation = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.product)
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder(Trade_.book)
                                .enableRowGroup(true)
                                .build(),
                        // numbers
                        ColDef.builder(Trade_.currentValue)
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        ColDef.builder(Trade_.previousValue)
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        // boolean
                        ColDef.builder(Trade_.isSold)
                                .allowedAggFuncs("bool_and")
                                .enableValue(true)
                                .build()
                )
                .registerCustomAggFunction("bool_and", (cb, expr) -> cb.function("BOOL_AND", Boolean.class, expr))
                .registerCustomAggFunction("stddev_pop", (cb, expr) -> cb.function("STDDEV_POP", BigDecimal.class, expr))
                .registerCustomAggFunction("stddev_samp", (cb, expr) -> cb.function("STDDEV_SAMP", BigDecimal.class, expr))
                .build();

        this.queryBuilderSuppressAggFilteredOnly = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.product)
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder(Trade_.book)
                                .enableRowGroup(true)
                                .build(),
                        // numbers
                        ColDef.builder(FieldPath.of(Trade_.submitter).to(Submitter_.id))
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        ColDef.builder(FieldPath.of(Trade_.submitterDeal).to(SubmitterDeal_.id))
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        // date
                        ColDef.builder(Trade_.birthDate)
                                .enableValue(true)
                                .filter(AgDateColumnFilter.forLocalDate())
                                .build()
                )
                .suppressAggFilteredOnly(true)
                .build();

        this.queryBuilderGroupAggFiltering = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.product)
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder(Trade_.book)
                                .enableRowGroup(true)
                                .build(),
                        // numbers
                        ColDef.builder(Trade_.currentValue)
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        ColDef.builder(Trade_.previousValue)
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        // date
                        ColDef.builder(Trade_.birthDate)
                                .enableValue(true)
                                .filter(AgDateColumnFilter.forLocalDate())
                                .build()
                )
                .groupAggFiltering(true)
                .build();
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        return this.queryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRowsSuppressAggFilteredOnly(ServerSideGetRowsRequest request) {
        return this.queryBuilderSuppressAggFilteredOnly.getRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRowsGroupAggFiltering(ServerSideGetRowsRequest request) {
        return this.queryBuilderGroupAggFiltering.getRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRowsCustomAggFunc(ServerSideGetRowsRequest request) {
        return this.queryBuilderCustomAggregation.getRows(request);
    }
}
