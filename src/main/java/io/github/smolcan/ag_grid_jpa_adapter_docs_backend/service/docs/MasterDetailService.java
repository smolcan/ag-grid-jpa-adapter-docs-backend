package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.*;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.column.FieldPath;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
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

    private final QueryBuilder<Submitter, Long, Trade> basicQueryBuilder;
    private final QueryBuilder<Submitter, Long, Trade> eagerQueryBuilder;
    private final QueryBuilder<Trade, Long, Trade> customDetailConditionQueryBuilder;
    private final QueryBuilder<Submitter, Long, Trade> dynamicDetailQueryBuilder;
    private final QueryBuilder<Trade, Long, Trade> treeDataMasterDetailQueryBuilder;
    private final QueryBuilder<Submitter, Long, Trade> alwaysAppliedDetailPredicateQueryBuilder;

    @Autowired
    public MasterDetailService(EntityManager entityManager) {

        this.basicQueryBuilder = QueryBuilder.builder(Submitter.class, AbstractEntity_.id, Trade.class, entityManager)
                .colDefs(
                        ColDef.builder(AbstractEntity_.id)
                                .build(),
                        ColDef.builder(Submitter_.name)
                                .build()
                )

                .masterDetail(true)
                .masterDetailParams(
                        QueryBuilder.MasterDetailParams.<Submitter, Long, Trade>builder()
                                .detailClass(Trade.class)
                                .detailColDefs(
                                        ColDef.builder(Trade_.tradeId)
                                                .build(),
                                        ColDef.builder(Trade_.product)
                                                .build(),
                                        ColDef.builder(Trade_.portfolio)
                                                .build()
                                )
                                .detailMasterReferenceField(Trade_.submitter)
                                .build()
                )
                .build();

        this.eagerQueryBuilder = QueryBuilder.builder(Submitter.class, AbstractEntity_.id, Trade.class, entityManager)
                .colDefs(
                        ColDef.builder(AbstractEntity_.id)
                                .build(),
                        ColDef.builder(Submitter_.name)
                                .build()
                )

                .masterDetail(true)
                .masterDetailLazy(false)
                .masterDetailRowDataFieldName("detailRows")
                .masterDetailParams(
                        QueryBuilder.MasterDetailParams.<Submitter, Long, Trade>builder()
                                .detailClass(Trade.class)
                                .detailColDefs(
                                        ColDef.builder(Trade_.tradeId)
                                                .build(),
                                        ColDef.builder(Trade_.product)
                                                .build(),
                                        ColDef.builder(Trade_.portfolio)
                                                .build()
                                )
                                .detailMasterReferenceField(Trade_.submitter)
                                .build()
                )
                .build();

        this.alwaysAppliedDetailPredicateQueryBuilder = QueryBuilder.builder(Submitter.class, AbstractEntity_.id, Trade.class, entityManager)
                .colDefs(
                        ColDef.builder(AbstractEntity_.id)
                                .build(),
                        ColDef.builder(Submitter_.name)
                                .build()
                )

                .masterDetail(true)
                .masterDetailParams(
                        QueryBuilder.MasterDetailParams.<Submitter, Long, Trade>builder()
                                .detailClass(Trade.class)
                                .detailColDefs(
                                        ColDef.builder(Trade_.tradeId)
                                                .build(),
                                        ColDef.builder(Trade_.product)
                                                .build(),
                                        ColDef.builder(Trade_.isSold)
                                                .build()
                                )
                                .detailMasterReferenceField(Trade_.submitter)
                                // detail rows can never contain an unsold trade
                                .alwaysAppliedDetailPredicate((cb, detailRoot) -> cb.isTrue(detailRoot.get(Trade_.isSold)))
                                .build()
                )
                .build();

        this.customDetailConditionQueryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, Trade.class, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .build(),
                        ColDef.builder(FieldPath.of(Trade_.submitter).to(AbstractEntity_.id))
                                .build(),
                        ColDef.builder(FieldPath.of(Trade_.submitter).to(Submitter_.name))
                                .build()
                )

                .masterDetail(true)
                .masterDetailParams(
                        QueryBuilder.MasterDetailParams.<Trade, Long, Trade>builder()
                                .detailClass(Trade.class)
                                .detailColDefs(
                                        ColDef.builder(Trade_.tradeId)
                                                .build(),
                                        ColDef.builder(FieldPath.of(Trade_.submitter).to(AbstractEntity_.id))
                                                .build(),
                                        ColDef.builder(FieldPath.of(Trade_.submitter).to(Submitter_.name))
                                                .build()
                                )
                                .createMasterRowPredicate((cb, detailRoot, masterRow) -> {
                                    // detail will have all the trades that have the same submitter
                                    @SuppressWarnings("unchecked")
                                    var submitterObj = (Map<String, Object>) masterRow.get("submitter");
                                    if (submitterObj == null || submitterObj.isEmpty()) {
                                        return cb.or();
                                    }

                                    Long submitterId = Optional.ofNullable(submitterObj.get("id")).map(String::valueOf).map(Long::parseLong).orElse(null);
                                    Path<?> path = detailRoot.get("submitter").get("id");
                                    if (submitterId == null) {
                                        return cb.isNull(path);
                                    } else {
                                        return cb.equal(path, submitterId);
                                    }
                                })
                                .build()
                )

                .build();


        this.dynamicDetailQueryBuilder = QueryBuilder.builder(Submitter.class, AbstractEntity_.id, Trade.class, entityManager)
                .colDefs(
                        ColDef.builder(AbstractEntity_.id)
                                .build(),
                        ColDef.builder(Submitter_.name)
                                .build()
                )

                .masterDetail(true)
                .dynamicMasterDetailParams(masterRow -> {
                    long submitterId = Long.parseLong(String.valueOf(masterRow.get("id")));
                    if (submitterId % 2 == 0) {
                        return QueryBuilder.MasterDetailParams.<Submitter, Long, Trade>builder()
                                .detailClass(Trade.class)
                                .detailColDefs(
                                        ColDef.builder(Trade_.tradeId)
                                                .build(),
                                        ColDef.builder(Trade_.product)
                                                .build()
                                )
                                .detailMasterReferenceField(Trade_.submitter)
                                .build();
                    } else {
                        return QueryBuilder.MasterDetailParams.<Submitter, Long, Trade>builder()
                                .detailClass(Trade.class)
                                .detailColDefs(
                                        ColDef.builder(Trade_.tradeId)
                                                .build(),
                                        ColDef.builder(Trade_.portfolio)
                                                .build()
                                )
                                .detailMasterReferenceField(Trade_.submitter)
                                .build();
                    }
                })
                .build();

        this.treeDataMasterDetailQueryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, Trade.class, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .build(),
                        ColDef.builder(Trade_.product)
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .build(),
                        ColDef.builder(FieldPath.of(Trade_.submitter).to(AbstractEntity_.id))
                                .build()
                )

                // tree data config
                .treeData(true)
                .treeDataStringToParentIdTypeConverter(Long::valueOf)
                .isServerSideGroupFieldName("hasChildren")
                .treeDataParentReferenceField(Trade_.parentTrade)
                .treeDataChildrenField(Trade_.childTrades)

                // master/detail config
                .masterDetail(true)
                .masterDetailParams(
                        QueryBuilder.MasterDetailParams.<Trade, Long, Trade>builder()
                                .detailClass(Trade.class)
                                .detailColDefs(
                                        ColDef.builder(Trade_.tradeId)
                                                .build(),
                                        ColDef.builder(Trade_.product)
                                                .build(),
                                        ColDef.builder(Trade_.portfolio)
                                                .build(),
                                        ColDef.builder(FieldPath.of(Trade_.submitter).to(AbstractEntity_.id))
                                                .build()
                                )
                                .createMasterRowPredicate((cb, detailRoot, masterRow) -> {
                                    // detail will have all the trades that have the same submitter
                                    @SuppressWarnings("unchecked")
                                    var submitterObj = (Map<String, Object>) masterRow.get("submitter");
                                    if (submitterObj == null || submitterObj.isEmpty()) {
                                        return cb.or();
                                    }

                                    Long submitterId = Optional.ofNullable(submitterObj.get("id")).map(String::valueOf).map(Long::parseLong).orElse(null);
                                    Path<?> path = detailRoot.get("submitter").get("id");
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
        return this.basicQueryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDetailRowData(Map<String, Object> masterRow) {
        return this.basicQueryBuilder.getDetailRowData(masterRow);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getAlwaysAppliedDetailPredicateRows(ServerSideGetRowsRequest request) {
        return this.alwaysAppliedDetailPredicateQueryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAlwaysAppliedDetailPredicateDetailRowData(Map<String, Object> masterRow) {
        return this.alwaysAppliedDetailPredicateQueryBuilder.getDetailRowData(masterRow);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getEagerRows(ServerSideGetRowsRequest request) {
        return this.eagerQueryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getCustomDetailConditionRows(ServerSideGetRowsRequest request) {
        return this.customDetailConditionQueryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCustomDetailConditionDetailRows(Map<String, Object> request) {
        return this.customDetailConditionQueryBuilder.getDetailRowData(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getDynamicRows(ServerSideGetRowsRequest request) {
        return this.dynamicDetailQueryBuilder.getRows(request);
    }


    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDynamicDetailRowData(Map<String, Object> masterRow) {
        return this.dynamicDetailQueryBuilder.getDetailRowData(masterRow);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getTreeDataRows(ServerSideGetRowsRequest request) {
        return this.treeDataMasterDetailQueryBuilder.getRows(request);
    }


    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTreeDataDetailRowData(Map<String, Object> masterRow) {
        return this.treeDataMasterDetailQueryBuilder.getDetailRowData(masterRow);
    }
}
