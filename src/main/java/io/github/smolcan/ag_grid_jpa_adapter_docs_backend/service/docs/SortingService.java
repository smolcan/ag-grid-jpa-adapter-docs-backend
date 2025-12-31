package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;

import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SortingService {
    
    private final QueryBuilder<Trade> sortingQueryBuilder;
    private final QueryBuilder<Trade> sortingAbsoluteQueryBuilder;

    @Autowired
    public SortingService(EntityManager entityManager) {
        this.sortingQueryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        // enabled sorting
                        ColDef.builder()
                                .field("tradeId")
                                .filter(false)
                                .sortable(true)
                                .build(),
                        // disabled sorting
                        ColDef.builder()
                                .field("product")
                                .filter(false)
                                .sortable(false)
                                .build(),
                        // disabled sorting - throws
                        ColDef.builder()
                                .field("portfolio")
                                .filter(false)
                                .sortable(false)
                                .build()
                        )
                .build();

        this.sortingAbsoluteQueryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        // enabled sorting
                        ColDef.builder()
                                .field("tradeId")
                                .filter(false)
                                .build(),
                        // disabled sorting
                        ColDef.builder()
                                .field("pl1")
                                .filter(false)
                                .build(),
                        // disabled sorting - throws
                        ColDef.builder()
                                .field("pl2")
                                .filter(false)
                                .build()
                )
                .build();
    }


    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        return this.sortingQueryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getAbsoluteRows(ServerSideGetRowsRequest request) {
        return this.sortingAbsoluteQueryBuilder.getRows(request);
    }
    
}
