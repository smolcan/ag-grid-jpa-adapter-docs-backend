package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade_;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgNumberColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgTextColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PivotingService {

    private final QueryBuilder<Trade, Long, Void> queryBuilder;
    private final QueryBuilder<Trade, Long, Void> limitQueryBuilder;
    private final QueryBuilder<Trade, Long, Void> filteringQueryBuilder;

    @Autowired
    public PivotingService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.product)
                                .enableRowGroup(true, key -> key)
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .enableRowGroup(true, key -> key)
                                .build(),


                        ColDef.builder(Trade_.book)
                                .enablePivot(true)
                                .build(),

                        ColDef.builder(Trade_.currentValue)
                                .enableValue(true)
                                .build(),
                        ColDef.builder(Trade_.previousValue)
                                .enableValue(true)
                                .build()
                )
                .build();

        this.limitQueryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.product)
                                .enableRowGroup(true, key -> key)
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .enableRowGroup(true, key -> key)
                                .build(),


                        ColDef.builder(Trade_.book)
                                .enablePivot(true)
                                .build(),
                        ColDef.builder(Trade_.bidType)
                                .enablePivot(true)
                                .build(),

                        ColDef.builder(Trade_.currentValue)
                                .enableValue(true)
                                .build(),
                        ColDef.builder(Trade_.previousValue)
                                .enableValue(true)
                                .build()
                )
                .pivotMaxGeneratedColumns(10)
                .build();

        // same pivot, with a filter on every column, so filtering can be tried out while pivoting
        this.filteringQueryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.product)
                                .enableRowGroup(true, key -> key)
                                .filter(new AgTextColumnFilter())
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .enableRowGroup(true, key -> key)
                                .filter(new AgTextColumnFilter())
                                .build(),


                        ColDef.builder(Trade_.book)
                                .enablePivot(true)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder(Trade_.currentValue)
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        ColDef.builder(Trade_.previousValue)
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build()
                )
                .build();
    }


    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        return this.queryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRowsLimitColGen(ServerSideGetRowsRequest request) {
        return this.limitQueryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRowsFiltering(ServerSideGetRowsRequest request) {
        return this.filteringQueryBuilder.getRows(request);
    }



}
