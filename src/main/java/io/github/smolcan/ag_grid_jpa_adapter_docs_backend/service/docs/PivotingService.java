package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.exceptions.OnPivotMaxColumnsExceededException;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PivotingService {

    private final QueryBuilder<Trade> queryBuilder;
    private final QueryBuilder<Trade> limitQueryBuilder;
    
    @Autowired
    public PivotingService(EntityManager entityManager) {
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
                                .enablePivot(true)
                                .build(),
                        
                        ColDef.builder()
                                .field("currentValue")
                                .enableValue(true)
                                .build(),
                        ColDef.builder()
                                .field("previousValue")
                                .enableValue(true)
                                .build()
                )
                .build();

        this.limitQueryBuilder = QueryBuilder.builder(Trade.class, entityManager)
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
                                .enablePivot(true)
                                .build(),
                        ColDef.builder()
                                .field("bidType")
                                .enablePivot(true)
                                .build(),

                        ColDef.builder()
                                .field("currentValue")
                                .enableValue(true)
                                .build(),
                        ColDef.builder()
                                .field("previousValue")
                                .enableValue(true)
                                .build()
                )
                .pivotMaxGeneratedColumns(10)
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
    
    
    
}
