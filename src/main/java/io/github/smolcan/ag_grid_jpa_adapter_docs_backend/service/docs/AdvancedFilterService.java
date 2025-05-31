package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdvancedFilterService {

    private final QueryBuilder<Trade> queryBuilder;

    @Autowired
    public AdvancedFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
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
    }
}
