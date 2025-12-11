package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;


import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto.CustomNumberFilter;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto.CustomNumberFilterParams;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;

import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomFilterService {

    private final QueryBuilder<Trade> queryBuilder;

    @Autowired
    public CustomFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .filter(
                                        new CustomNumberFilter()
                                )
                                .build(),
                        
                        ColDef.builder()
                                .field("submitterDeal.id")
                                .filter(
                                        new CustomNumberFilter()
                                                .filterParams(
                                                        CustomNumberFilterParams.builder()
                                                                .includeNullValues(true)
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
}
