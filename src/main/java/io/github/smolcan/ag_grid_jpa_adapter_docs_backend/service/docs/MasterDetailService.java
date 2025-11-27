package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.exceptions.OnPivotMaxColumnsExceededException;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class MasterDetailService {

    private final QueryBuilder<Trade> queryBuilder;

    @Autowired
    public MasterDetailService(EntityManager entityManager) {

        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .build(),
                        ColDef.builder()
                                .field("product")
                                .build(),
                        ColDef.builder()
                                .field("portfolio")
                                .build()
                )
                
                .masterDetail(true)
                .primaryFieldName("tradeId")
                .detailClass(Trade.class)
                .detailColDefs(
                        ColDef.builder()
                                .field("parentTrade.tradeId")
                                .build(),
                        ColDef.builder()
                                .field("product")
                                .build(),
                        ColDef.builder()
                                .field("portfolio")
                                .build()
                )
//                .createMasterRowPredicate((cb, detailRoot, masterRow) -> {
//                    // same product
//                    Object masterRowProduct = masterRow.get("product");
//                    return cb.equal(detailRoot.get("product"), masterRowProduct);
//                })
                .detailMasterReferenceField("parentTrade")
                
                
                .build();
    }


    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        try {
            return this.queryBuilder.getRows(request);
        } catch (OnPivotMaxColumnsExceededException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDetailRowData(Map<String, Object> masterRow) {
        try {
            return this.queryBuilder.getDetailRowData(masterRow);
        } catch (OnPivotMaxColumnsExceededException e) {
            throw new RuntimeException(e);
        }
    }
}
