package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Submitter;
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

    private final QueryBuilder<Submitter> basicQueryBuilder;
    private final QueryBuilder<Submitter> eagerQueryBuilder;
    private final QueryBuilder<Submitter> dynamicDetailQueryBuilder;

    @Autowired
    public MasterDetailService(EntityManager entityManager) {

        this.basicQueryBuilder = QueryBuilder.builder(Submitter.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("id")
                                .build(),
                        ColDef.builder()
                                .field("name")
                                .build()
                )
                
                .masterDetail(true)
                .primaryFieldName("id")
                .masterDetailParams(
                        QueryBuilder.MasterDetailParams.builder()
                                .detailClass(Trade.class)
                                .detailColDefs(
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
                                .detailMasterReferenceField("submitter")
                                .build()
                )
                .build();

        this.eagerQueryBuilder = QueryBuilder.builder(Submitter.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("id")
                                .build(),
                        ColDef.builder()
                                .field("name")
                                .build()
                )

                .masterDetail(true)
                .masterDetailLazy(false)
                .masterDetailRowDataFieldName("detailRows")
                .primaryFieldName("id")
                .masterDetailParams(
                        QueryBuilder.MasterDetailParams.builder()
                                .detailClass(Trade.class)
                                .detailColDefs(
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
                                .detailMasterReferenceField("submitter")
                                .build()
                )
                .build();
        
        
        this.dynamicDetailQueryBuilder = QueryBuilder.builder(Submitter.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("id")
                                .build(),
                        ColDef.builder()
                                .field("name")
                                .build()
                )

                .masterDetail(true)
                .primaryFieldName("id")
                .dynamicMasterDetailParams((masterRow) -> {
                    long submitterId = Long.parseLong(String.valueOf(masterRow.get("id")));
                    if (submitterId % 2 == 0) {
                        return QueryBuilder.MasterDetailParams.builder()
                                .detailClass(Trade.class)
                                .detailColDefs(
                                        ColDef.builder()
                                                .field("tradeId")
                                                .build(),
                                        ColDef.builder()
                                                .field("product")
                                                .build()
                                )
                                .detailMasterReferenceField("submitter")
                                .build();
                    } else {
                        return QueryBuilder.MasterDetailParams.builder()
                                .detailClass(Trade.class)
                                .detailColDefs(
                                        ColDef.builder()
                                                .field("tradeId")
                                                .build(),
                                        ColDef.builder()
                                                .field("portfolio")
                                                .build()
                                )
                                .detailMasterReferenceField("submitter")
                                .build();
                    }
                })
                .build();
    }


    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        try {
            return this.basicQueryBuilder.getRows(request);
        } catch (OnPivotMaxColumnsExceededException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDetailRowData(Map<String, Object> masterRow) {
        try {
            return this.basicQueryBuilder.getDetailRowData(masterRow);
        } catch (OnPivotMaxColumnsExceededException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getEagerRows(ServerSideGetRowsRequest request) {
        try {
            return this.eagerQueryBuilder.getRows(request);
        } catch (OnPivotMaxColumnsExceededException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Transactional(readOnly = true)
    public LoadSuccessParams getDynamicRows(ServerSideGetRowsRequest request) {
        try {
            return this.dynamicDetailQueryBuilder.getRows(request);
        } catch (OnPivotMaxColumnsExceededException e) {
            throw new RuntimeException(e);
        }
    }
    

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDynamicDetailRowData(Map<String, Object> masterRow) {
        try {
            return this.dynamicDetailQueryBuilder.getDetailRowData(masterRow);
        } catch (OnPivotMaxColumnsExceededException e) {
            throw new RuntimeException(e);
        }
    }
}
