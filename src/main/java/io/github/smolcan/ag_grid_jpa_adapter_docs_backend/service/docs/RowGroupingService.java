package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.exceptions.OnPivotMaxColumnsExceededException;
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
public class RowGroupingService {

    private final QueryBuilder<Trade> queryBuilder;
    
    @Autowired
    public RowGroupingService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .filter(new AgNumberColumnFilter())
                                .build(),

                        ColDef.builder()
                                .field("portfolio")
                                .enableRowGroup(true)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder()
                                .field("product")
                                .enableRowGroup(true)
                                .filter(new AgTextColumnFilter())
                                .build()
                )
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
}
