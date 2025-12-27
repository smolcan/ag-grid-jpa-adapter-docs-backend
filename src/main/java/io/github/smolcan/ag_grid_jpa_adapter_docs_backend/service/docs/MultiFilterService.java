package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;

import io.github.smolcan.aggrid.jpa.adapter.filter.model.simple.params.MultiFilterParams;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.AgMultiColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.AgSetColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgDateColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgTextColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class MultiFilterService {
    
    private final QueryBuilder<Trade> queryBuilder;

    @Autowired
    public MultiFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .filter(false)
                                .build(),

                        ColDef.builder()
                                .field("product")
                                .filter(
                                        new AgMultiColumnFilter()
                                                .filterParams(
                                                        MultiFilterParams.builder()
                                                                .filters(
                                                                        new AgTextColumnFilter(),
                                                                        new AgSetColumnFilter()
                                                                )
                                                                .build()
                                                )
                                )
                                .build(),

                        ColDef.builder()
                                .field("birthDate")
                                .filter(
                                        new AgMultiColumnFilter()
                                                .filterParams(
                                                        MultiFilterParams.builder()
                                                                .filters(
                                                                        new AgDateColumnFilter(),
                                                                        new AgSetColumnFilter()
                                                                )
                                                                .build()
                                                )
                                )
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
