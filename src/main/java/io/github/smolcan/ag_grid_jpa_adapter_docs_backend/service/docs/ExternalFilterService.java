package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;


import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgNumberColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import io.github.smolcan.aggrid.jpa.adapter.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExternalFilterService {

    private final QueryBuilder<Trade> queryBuilder;

    @Autowired
    public ExternalFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .filter(new AgNumberColumnFilter())
                                .build(),
                        ColDef.builder()
                                .field("submitter.id")
                                .filter(new AgNumberColumnFilter())
                                .build(),
                        ColDef.builder()
                                .field("submitterDeal.id")
                                .filter(new AgNumberColumnFilter())
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
                            Path<?> submitterIdPath = Utils.getPath(root, "submitter.id");
                            return cb.notEqual(cb.mod((Expression) submitterIdPath, 2), 0);
                        }
                        case "Submitter Id Even" -> {
                            Path<?> submitterIdPath = Utils.getPath(root, "submitter.id");
                            return cb.equal(cb.mod((Expression) submitterIdPath, 2), 0);
                        }
                        case "Submitter Deal Id Odd" -> {
                            Path<?> submitterDealIdPath = Utils.getPath(root, "submitterDeal.id");
                            return cb.notEqual(cb.mod((Expression) submitterDealIdPath, 2), 0);
                        }
                        case "Submitter Deal Id Even" -> {
                            Path<?> submitterDealIdPath = Utils.getPath(root, "submitterDeal.id");
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
