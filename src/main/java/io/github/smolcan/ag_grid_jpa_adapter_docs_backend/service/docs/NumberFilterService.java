package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.exceptions.OnPivotMaxColumnsExceededException;
import io.github.smolcan.aggrid.jpa.adapter.filter.model.simple.params.NumberFilterParams;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgNumberColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NumberFilterService {

    private final QueryBuilder<Trade> queryBuilder;


    @Autowired
    public NumberFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .filter(
                                        new AgNumberColumnFilter()
                                )
                                .build(),
                        
                        ColDef.builder()
                                .field("submitterId")
                                .filter(
                                        new AgNumberColumnFilter()
                                                .filterParams(NumberFilterParams
                                                        .builder()
                                                        .inRangeInclusive(true)
                                                        .build()
                                                )
                                )
                                .build(),

                        ColDef.builder()
                                .field("submitterDealId")
                                .filter(
                                        new AgNumberColumnFilter()
                                                .filterParams(NumberFilterParams
                                                        .builder()
                                                        .includeBlanksInEquals(true)
                                                        .includeBlanksInNotEqual(true)
                                                        .build()
                                                )
                                )
                                .build(),

                        ColDef.builder()
                                .field("currentValue")
                                .filter(
                                        new AgNumberColumnFilter()
                                                .filterParams(NumberFilterParams
                                                        .builder()
                                                        .includeBlanksInLessThan(true)
                                                        .includeBlanksInGreaterThan(true)
                                                        .build()
                                                )
                                )
                                .build(),

                        ColDef.builder()
                                .field("previousValue")
                                .filter(
                                        new AgNumberColumnFilter()
                                                .filterParams(NumberFilterParams
                                                        .builder()
                                                        .includeBlanksInRange(true)
                                                        .build()
                                                )
                                )
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
