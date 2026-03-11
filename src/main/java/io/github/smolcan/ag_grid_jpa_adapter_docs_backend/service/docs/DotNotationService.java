package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;


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
public class DotNotationService {

    private final QueryBuilder<Trade> queryBuilder;
    private final QueryBuilder<Trade> flatDataQueryBuilder;
    
    @Autowired
    public DotNotationService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .filter(false)
                                .build(),
                        ColDef.builder()
                                .field("submitter.id")
                                .filter(false)
                                .build(),
                        ColDef.builder()
                                .field("submitterDeal.id")
                                .filter(false)
                                .build(),
                        ColDef.builder()
                                .field("parentTrade.id")
                                .filter(false)
                                .build(),
                        ColDef.builder()
                                .field("parentTrade.parentTrade.id")
                                .filter(false)
                                .build()
                )
                .build();

        this.flatDataQueryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .filter(false)
                                .build(),
                        ColDef.builder()
                                .field("submitter.id")
                                .filter(false)
                                .build(),
                        ColDef.builder()
                                .field("submitterDeal.id")
                                .filter(false)
                                .build(),
                        ColDef.builder()
                                .field("parentTrade.id")
                                .filter(false)
                                .build(),
                        ColDef.builder()
                                .field("parentTrade.parentTrade.id")
                                .filter(false)
                                .build()
                )
                .suppressFieldDotNotation(true)
                .build();
    }


    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        return this.queryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getFlatDataRows(ServerSideGetRowsRequest request) {
        return this.flatDataQueryBuilder.getRows(request);
    }
    
}
