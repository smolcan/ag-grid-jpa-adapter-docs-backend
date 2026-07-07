package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;


import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Submitter_;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade_;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.column.FieldPath;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgNumberColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgTextColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuickFilterService {

    private final QueryBuilder<Trade, Long, Void> queryBuilder;

    @Autowired
    public QuickFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),

                        ColDef.builder(FieldPath.of(Trade_.submitter).to(Submitter_.name))
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder(Trade_.portfolio)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder(Trade_.product)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder(Trade_.book)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder(Trade_.dealType)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        ColDef.builder(Trade_.bidType)
                                .filter(new AgTextColumnFilter())
                                .build()
                )

                .isQuickFilterPresent(true)
                .quickFilterSearchInFields(
                        FieldPath.of(Trade_.submitter).to(Submitter_.name),
                        FieldPath.of(Trade_.product),
                        FieldPath.of(Trade_.portfolio),
                        FieldPath.of(Trade_.book),
                        FieldPath.of(Trade_.dealType),
                        FieldPath.of(Trade_.bidType)
                )
                .quickFilterTextFormatter((cb, stringExpr) -> {
                    Expression<String> newExpression = stringExpr;
                    // Remove accents
                    newExpression = cb.function("TRANSLATE", String.class, newExpression,
                            cb.literal("áéíóúÁÉÍÓÚüÜñÑ"),
                            cb.literal("aeiouAEIOUuUnN"));

                    return newExpression;
                })

                .build();
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        return this.queryBuilder.getRows(request);
    }

}
