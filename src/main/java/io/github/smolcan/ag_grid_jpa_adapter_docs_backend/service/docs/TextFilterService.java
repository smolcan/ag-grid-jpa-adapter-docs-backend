package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;

import io.github.smolcan.aggrid.jpa.adapter.filter.model.simple.params.TextFilterParams;
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
public class TextFilterService {

    private final QueryBuilder<Trade> queryBuilder;

    @Autowired
    public TextFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .build(),
                        
                        ColDef.builder()
                                .field("portfolio")
                                .filter(new AgTextColumnFilter())
                                .build(),
                        
                        ColDef.builder()
                                .field("product")
                                .filter(
                                        new AgTextColumnFilter()
                                                .filterParams(
                                                        TextFilterParams.builder()
                                                                .caseSensitive(true)
                                                                .build()
                                                )
                                )
                                .build(),
                        
                        ColDef.builder()
                                .field("book")
                                .filter(
                                        new AgTextColumnFilter()
                                                .filterParams(
                                                        TextFilterParams.builder()
                                                                .trimInput(true)
                                                                .build()
                                                )
                                )
                                .build(),

                        ColDef.builder()
                                .field("dealType")
                                .filter(
                                        new AgTextColumnFilter()
                                                .filterParams(
                                                        TextFilterParams.builder()
                                                                .textFormatter((cb, expr) -> {
                                                                    Expression<String> newExpression = expr;
                                                                    // lower input
                                                                    newExpression = cb.lower(newExpression);
                                                                    // Remove accents
                                                                    newExpression = cb.function("TRANSLATE", String.class, newExpression,
                                                                            cb.literal("áéíóúÁÉÍÓÚüÜñÑýÝ"),
                                                                            cb.literal("aeiouAEIOUuUnNyY"));
                                                                    
                                                                    return newExpression;
                                                                })
                                                                .build()
                                                )
                                )
                                .build(),

                        ColDef.builder()
                                .field("bidType")
                                .filter(
                                        new AgTextColumnFilter()
                                                .filterParams(
                                                        TextFilterParams.builder()
                                                                .textMatcher((cb, textMatcherParams) -> {
                                                                    return cb.equal(textMatcherParams.getFilterText(), "bid");
                                                                })
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
