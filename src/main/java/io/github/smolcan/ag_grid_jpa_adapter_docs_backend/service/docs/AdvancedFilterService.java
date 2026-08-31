package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.AbstractEntity_;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade_;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;

import io.github.smolcan.aggrid.jpa.adapter.column.FieldPath;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.AgSetColumnFilter;
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
public class AdvancedFilterService {

    private final QueryBuilder<Trade, Long, Void> queryBuilder;

    @Autowired
    public AdvancedFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        // strings
                        ColDef.builder(Trade_.product)
                                .filter(new AgTextColumnFilter())
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .filter(new AgTextColumnFilter())
                                .build(),
                        ColDef.builder(Trade_.book)
                                .filter(new AgTextColumnFilter())
                                .build(),
                        // numbers
                        ColDef.builder(FieldPath.of(Trade_.submitter).to(AbstractEntity_.id))
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        ColDef.builder(FieldPath.of(Trade_.submitterDeal).to(AbstractEntity_.id))
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        // date
                        ColDef.builder(Trade_.birthDate)
                                .filter(AgDateColumnFilter.forLocalDate())
                                .build(),
                        // boolean
                        ColDef.builder(Trade_.isSold)
                                .filter(AgSetColumnFilter.forBoolean())
                                .build()
                )
                .enableAdvancedFilter(true)
                .build();
    }
    
    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        return this.queryBuilder.getRows(request);
    }
}
