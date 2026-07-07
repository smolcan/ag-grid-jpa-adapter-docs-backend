package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.SubmitterDeal_;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Submitter_;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade_;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;

import io.github.smolcan.aggrid.jpa.adapter.column.FieldPath;
import io.github.smolcan.aggrid.jpa.adapter.filter.model.simple.params.NumberFilterParams;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgNumberColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class NumberFilterService {

    private final QueryBuilder<Trade, Void> queryBuilder;


    @Autowired
    public NumberFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .filter(
                                        new AgNumberColumnFilter<>()
                                )
                                .build(),
                        
                        ColDef.builder(FieldPath.of(Trade_.submitter).to(Submitter_.id))
                                .filter(
                                        new AgNumberColumnFilter<Long>()
                                                .filterParams(NumberFilterParams
                                                        .builder()
                                                        .inRangeInclusive(true)
                                                        .build()
                                                )
                                )
                                .build(),

                        ColDef.builder(FieldPath.of(Trade_.submitterDeal).to(SubmitterDeal_.id))
                                .filter(
                                        new AgNumberColumnFilter<Long>()
                                                .filterParams(NumberFilterParams
                                                        .builder()
                                                        .includeBlanksInEquals(true)
                                                        .includeBlanksInNotEqual(true)
                                                        .build()
                                                )
                                )
                                .build(),

                        ColDef.builder(Trade_.currentValue)
                                .filter(
                                        new AgNumberColumnFilter<BigDecimal>()
                                                .filterParams(NumberFilterParams
                                                        .builder()
                                                        .includeBlanksInLessThan(true)
                                                        .includeBlanksInGreaterThan(true)
                                                        .build()
                                                )
                                )
                                .build(),

                        ColDef.builder(Trade_.previousValue)
                                .filter(
                                        new AgNumberColumnFilter<BigDecimal>()
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
        return this.queryBuilder.getRows(request);
    }
    
}
