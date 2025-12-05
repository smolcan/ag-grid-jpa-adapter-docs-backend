package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;


import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
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
public class QuickFilterService {
    
    private final QueryBuilder<Trade> queryBuilder;

    @Autowired
    public QuickFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .filter(new AgNumberColumnFilter())
                                .build(),

                        ColDef.builder()
                                .field("submitter.name")
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder()
                                .field("portfolio")
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder()
                                .field("product")
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder()
                                .field("book")
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder()
                                .field("dealType")
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder()
                                .field("bidType")
                                .filter(new AgTextColumnFilter())
                                .build()
                )
                
                .isQuickFilterPresent(true)
                .quickFilterSearchInFields("submitter.name", "product", "portfolio", "book", "dealType", "bidType")
                
                .build();
    }
    
    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        return this.queryBuilder.getRows(request);
    }
    
}
