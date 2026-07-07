package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade_;
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

import java.time.LocalDate;
import java.util.List;


@Service
public class MultiFilterService {

    private final QueryBuilder<Trade, Long, Void> queryBuilder;

    @Autowired
    public MultiFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .build(),

                        ColDef.builder(Trade_.product)
                                .filter(
                                        new AgMultiColumnFilter<String>()
                                                .filterParams(
                                                        MultiFilterParams.<String>builder()
                                                                .filters(
                                                                        new AgTextColumnFilter(),
                                                                        AgSetColumnFilter.forString()
                                                                )
                                                                .build()
                                                )
                                )
                                .build(),

                        ColDef.builder(Trade_.birthDate)
                                .filter(
                                        new AgMultiColumnFilter<LocalDate>()
                                                .filterParams(
                                                        MultiFilterParams.<LocalDate>builder()
                                                                .filters(
                                                                        AgDateColumnFilter.forLocalDate(),
                                                                        AgSetColumnFilter.forDate()
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
