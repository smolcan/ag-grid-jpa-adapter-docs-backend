package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;


import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;

import io.github.smolcan.aggrid.jpa.adapter.filter.model.simple.params.DateFilterParams;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgDateColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DateFilterService {

    private final QueryBuilder<Trade> queryBuilder;

    @Autowired
    public DateFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .filter(false)
                                .build(),
                        
                        ColDef.builder()
                                .field("birthDate")
                                .filter(
                                        new AgDateColumnFilter()
                                                .filterParams(
                                                        DateFilterParams.builder()
                                                                .inRangeInclusive(true)
                                                                .includeBlanksInEquals(true)
                                                                .includeBlanksInNotEqual(true)
                                                                .includeBlanksInLessThan(true)
                                                                .includeBlanksInGreaterThan(true)
                                                                .includeBlanksInRange(true)
                                                                .maxValidYear(LocalDate.now().getYear() + 1)
                                                                .minValidYear(LocalDate.now().getYear() - 1)
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
