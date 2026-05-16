package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
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

import java.util.List;
import java.util.Map;

@Service
public class GrandTotalRowService {
    
    private final QueryBuilder<Trade> queryBuilder;
    private final QueryBuilder<Trade> queryBuilderAsync;
    
    @Autowired
    public GrandTotalRowService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        
                        ColDef.builder()
                                .field("product")
                                .enableRowGroup(true)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder()
                                .field("book")
                                .filter(new AgSetColumnFilter())
                                .build(),
                        
                        ColDef.builder()
                                .field("currentValue")
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),
                        ColDef.builder()
                                .field("previousValue")
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter())
                                .build()
                        )
                .grandTotalRow(true)
                .build();

        this.queryBuilderAsync = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .filter(false)
                                .build(),

                        ColDef.builder()
                                .field("book")
                                .filter(new AgSetColumnFilter())
                                .build(),

                        ColDef.builder()
                                .field("currentValue")
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),
                        ColDef.builder()
                                .field("previousValue")
                                .enableValue(true)
                                .filter(new AgNumberColumnFilter())
                                .build()
                )
                .grandTotalRow(true)
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

    @Transactional(readOnly = true)
    public LoadSuccessParams getRowsAsync(ServerSideGetRowsRequest request) {
        return this.queryBuilderAsync.getRows(request);
    }

    @Transactional(readOnly = true)
    public List<Object> supplySetFilterValuesAsync(String field) {
        return this.queryBuilderAsync.supplySetFilterValues(field);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getGrandTotalDataAsync(ServerSideGetRowsRequest request) {
        return this.queryBuilderAsync.getGrandTotalData(request);
    }
}
