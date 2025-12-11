package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;

import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgDateColumnFilter;
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
public class PaginationService {

    private final QueryBuilder<Trade> queryBuilder;
    private final QueryBuilder<Trade> paginateChildRowsQueryBuilder;
    
    @Autowired
    public PaginationService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .filter(
                                        new AgNumberColumnFilter()
                                )
                                .build(),

                        ColDef.builder()
                                .field("submitter.id")
                                .filter(
                                        new AgNumberColumnFilter()
                                )
                                .build(),

                        ColDef.builder()
                                .field("portfolio")
                                .enableRowGroup(true)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder()
                                .field("birthDate")
                                .filter(
                                        new AgDateColumnFilter()
                                )
                                .build()
                )
                .build();

        this.paginateChildRowsQueryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .filter(
                                        new AgNumberColumnFilter()
                                )
                                .build(),

                        ColDef.builder()
                                .field("submitter.id")
                                .filter(
                                        new AgNumberColumnFilter()
                                )
                                .build(),

                        ColDef.builder()
                                .field("portfolio")
                                .enableRowGroup(true)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder()
                                .field("birthDate")
                                .filter(
                                        new AgDateColumnFilter()
                                )
                                .build()
                )
                .paginateChildRows(true)
                .build();
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        return this.queryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public long countRows(ServerSideGetRowsRequest request) {
        return this.queryBuilder.countRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams paginateChildRowsGetRows(ServerSideGetRowsRequest request) {
        return this.paginateChildRowsQueryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public long paginateChildRowsCountRows(ServerSideGetRowsRequest request) {
        return this.paginateChildRowsQueryBuilder.countRows(request);
    }
}
