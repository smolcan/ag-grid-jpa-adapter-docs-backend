package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Statistics_;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade_;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.column.FieldPath;
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

    private final QueryBuilder<Trade, Void> sortingQueryBuilder;
    private final QueryBuilder<Trade, Void> sortingAbsoluteQueryBuilder;

    @Autowired
    public SortingService(EntityManager entityManager) {
        this.sortingQueryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        // enabled sorting
                        ColDef.builder(Trade_.tradeId)
                                .sortable(true)
                                .build(),
                        // disabled sorting
                        ColDef.builder(Trade_.product)
                                .sortable(false)
                                .build(),
                        // disabled sorting - throws
                        ColDef.builder(Trade_.portfolio)
                                .sortable(false)
                                .build()
                        )
                .build();

        this.sortingAbsoluteQueryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        // enabled sorting
                        ColDef.builder(Trade_.tradeId)
                                .build(),
                        // disabled sorting
                        ColDef.builder(FieldPath.of(Trade_.statistics).to(Statistics_.pl1))
                                .build(),
                        // disabled sorting - throws
                        ColDef.builder(FieldPath.of(Trade_.statistics).to(Statistics_.pl2))
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
