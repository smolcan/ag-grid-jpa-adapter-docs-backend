package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Submitter;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.exceptions.OnPivotMaxColumnsExceededException;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import io.github.smolcan.aggrid.jpa.adapter.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MasterDetailService {

    private final QueryBuilder<Submitter> basicQueryBuilder;
    private final QueryBuilder<Submitter> eagerQueryBuilder;
    private final QueryBuilder<Submitter> dynamicDetailQueryBuilder;
    private final QueryBuilder<Trade> treeDataMasterDetailQueryBuilder;

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
        
        this.treeDataMasterDetailQueryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .build(),
                        ColDef.builder()
                                .field("product")
                                .build(),
                        ColDef.builder()
                                .field("portfolio")
                                .build(),
                        ColDef.builder()
                                .field("submitter.id")
                                .build()
                )
                
                // tree data config
                .treeData(true)
                .primaryFieldName("tradeId")
                .isServerSideGroupFieldName("hasChildren")
                .treeDataParentReferenceField("parentTrade")
                .treeDataChildrenField("childTrades")
                
                // master/detail config
                .masterDetail(true)
                .primaryFieldName("tradeId")
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
                                                .build(),
                                        ColDef.builder()
                                                .field("submitter.id")
                                                .build()
                                )
                                .createMasterRowPredicate((cb, detailRoot, masterRow) -> {
                                    // detail will have all the trades that have the same submitter
                                    var submitterObj = (Map<String, Object>) masterRow.get("submitter");
                                    if (submitterObj == null || submitterObj.isEmpty()) {
                                        return cb.or();
                                    }
                                    
                                    Long submitterId = Optional.ofNullable(submitterObj.get("id")).map(String::valueOf).map(Long::parseLong).orElse(null);
                                    Path<?> path = Utils.getPath(detailRoot, "submitter.id");
                                    if (submitterId == null) {
                                        return cb.isNull(path);
                                    } else {
                                        return cb.equal(path, submitterId);
                                    }
                                })
                                .build()
                )
                
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

    @Transactional(readOnly = true)
    public LoadSuccessParams getTreeDataRows(ServerSideGetRowsRequest request) {
        try {
            return this.treeDataMasterDetailQueryBuilder.getRows(request);
        } catch (OnPivotMaxColumnsExceededException e) {
            throw new RuntimeException(e);
        }
    }


    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTreeDataDetailRowData(Map<String, Object> masterRow) {
        try {
            return this.treeDataMasterDetailQueryBuilder.getDetailRowData(masterRow);
        } catch (OnPivotMaxColumnsExceededException e) {
            throw new RuntimeException(e);
        }
    }
}
