package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.AbstractEntity_;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade_;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;

import io.github.smolcan.aggrid.jpa.adapter.column.FieldPath;
import io.github.smolcan.aggrid.jpa.adapter.filter.model.simple.params.SetFilterParams;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.AgSetColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class SetFilterService {
    private final QueryBuilder<Trade, Long, Void> queryBuilder;

    @Autowired
    public SetFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .build(),

                        ColDef.builder(Trade_.product)
                                .filter(AgSetColumnFilter.forString())
                                .build(),

                        ColDef.builder(Trade_.portfolio)
                                .filter(
                                        AgSetColumnFilter.forString()
                                                .filterParams(
                                                        SetFilterParams
                                                                .builder()
                                                                .caseSensitive(true)
                                                                .build()
                                                )
                                )
                                .build(),

                        ColDef.builder(Trade_.book)
                                .filter(
                                        AgSetColumnFilter.forString()
                                                .filterParams(
                                                        SetFilterParams
                                                                .builder()
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

                        ColDef.builder(FieldPath.of(Trade_.submitter).to(AbstractEntity_.id))
                                .filter(AgSetColumnFilter.forNumber())
                                .build(),

                        ColDef.builder(Trade_.birthDate)
                                .filter(AgSetColumnFilter.forDate())
                                .build(),

                        ColDef.builder(Trade_.isSold)
                                .filter(AgSetColumnFilter.forBoolean())
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
