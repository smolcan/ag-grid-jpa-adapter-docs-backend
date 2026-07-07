package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;


import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Submitter_;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.SubmitterDeal_;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade_;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.column.FieldPath;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgNumberColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExternalFilterService {

    private final QueryBuilder<Trade, Void> queryBuilder;

    @Autowired
    public ExternalFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        ColDef.builder(FieldPath.of(Trade_.submitter).to(Submitter_.id))
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        ColDef.builder(FieldPath.of(Trade_.submitterDeal).to(SubmitterDeal_.id))
                                .filter(new AgNumberColumnFilter<>())
                                .build()
                )
                .isExternalFilterPresent(true)
                .doesExternalFilterPass((cb, root, externalFilterValue) -> {
                    if (externalFilterValue == null) {
                        return null;
                    }

                    String externalFilter = (String) externalFilterValue;
                    switch (externalFilter) {
                        case "Submitter Id Odd" -> {
                            Path<?> submitterIdPath = root.get("submitter").get("id");
                            return cb.notEqual(cb.mod((Expression) submitterIdPath, 2), 0);
                        }
                        case "Submitter Id Even" -> {
                            Path<?> submitterIdPath = root.get("submitter").get("id");
                            return cb.equal(cb.mod((Expression) submitterIdPath, 2), 0);
                        }
                        case "Submitter Deal Id Odd" -> {
                            Path<?> submitterDealIdPath = root.get("submitterDeal").get("id");
                            return cb.notEqual(cb.mod((Expression) submitterDealIdPath, 2), 0);
                        }
                        case "Submitter Deal Id Even" -> {
                            Path<?> submitterDealIdPath = root.get("submitterDeal").get("id");
                            return cb.equal(cb.mod((Expression) submitterDealIdPath, 2), 0);
                        }
                        default -> {
                            return null;
                        }
                    }
                })
                .build();
    }


    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        return this.queryBuilder.getRows(request);
    }
}
