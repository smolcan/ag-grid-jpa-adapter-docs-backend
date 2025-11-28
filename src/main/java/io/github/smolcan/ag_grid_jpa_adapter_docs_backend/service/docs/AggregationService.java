package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.exceptions.OnPivotMaxColumnsExceededException;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgDateColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgNumberColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AggregationService {

    private final QueryBuilder<Trade> queryBuilder;
    private final QueryBuilder<Trade> queryBuilderSuppressAggFilteredOnly;
    private final QueryBuilder<Trade> queryBuilderGroupAggFiltering;

    @Autowired
    public AggregationService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("product")
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder()
                                .field("portfolio")
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder()
                                .field("book")
                                .enableRowGroup(true)
                                .build(),
                        // numbers
                        ColDef.builder()
                                .field("submitter.id")
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),
                        ColDef.builder()
                                .field("submitterDeal.id")
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),
                        // date
                        ColDef.builder()
                                .field("birthDate")
                                .enableValue(true)
                                .filter(new AgDateColumnFilter())
                                .build()
                )
                .build();

        this.queryBuilderSuppressAggFilteredOnly = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("product")
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder()
                                .field("portfolio")
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder()
                                .field("book")
                                .enableRowGroup(true)
                                .build(),
                        // numbers
                        ColDef.builder()
                                .field("submitter.id")
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),
                        ColDef.builder()
                                .field("submitterDeal.id")
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),
                        // date
                        ColDef.builder()
                                .field("birthDate")
                                .enableValue(true)
                                .filter(new AgDateColumnFilter())
                                .build()
                )
                .suppressAggFilteredOnly(true)
                .build();

        this.queryBuilderGroupAggFiltering = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("product")
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder()
                                .field("portfolio")
                                .enableRowGroup(true)
                                .build(),
                        ColDef.builder()
                                .field("book")
                                .enableRowGroup(true)
                                .build(),
                        // numbers
                        ColDef.builder()
                                .field("submitter.id")
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),
                        ColDef.builder()
                                .field("submitterDeal.id")
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),
                        // date
                        ColDef.builder()
                                .field("birthDate")
                                .enableValue(true)
                                .filter(new AgDateColumnFilter())
                                .build()
                )
                .groupAggFiltering(true)
                .build();
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        try {
            return this.queryBuilder.getRows(request);
        } catch (OnPivotMaxColumnsExceededException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRowsSuppressAggFilteredOnly(ServerSideGetRowsRequest request) {
        try {
            return this.queryBuilderSuppressAggFilteredOnly.getRows(request);
        } catch (OnPivotMaxColumnsExceededException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRowsGroupAggFiltering(ServerSideGetRowsRequest request) {
        try {
            return this.queryBuilderGroupAggFiltering.getRows(request);
        } catch (OnPivotMaxColumnsExceededException e) {
            throw new RuntimeException(e);
        }
    }
    
}
