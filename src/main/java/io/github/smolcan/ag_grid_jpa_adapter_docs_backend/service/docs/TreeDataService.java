package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TreeDataService {

    private final QueryBuilder<Trade, Long, Void> queryBuilder;
    private final QueryBuilder<Trade, Long, Void> childCountQueryBuilder;
    private final QueryBuilder<Trade, Long, Void> aggregationTreeQueryBuilder;
    private final QueryBuilder<Trade, Long, Void> filteringQueryBuilder;
    private final QueryBuilder<Trade, Long, Void> filteringAllQueryBuilder;

    @Autowired
    public TreeDataService(EntityManager entityManager) {

        this.queryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .build(),
                        ColDef.builder(Trade_.product)
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .build(),
                        ColDef.builder(Trade_.currentValue)
                                .build(),
                        ColDef.builder(Trade_.previousValue)
                                .build()
                )

                .treeData(true)
                .isServerSideGroupFieldName("hasChildren")
                .treeDataParentReferenceField(Trade_.parentTrade)
                .treeDataChildrenField(Trade_.childTrades)

                .build();

        this.childCountQueryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .build(),
                        ColDef.builder(Trade_.product)
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .build(),
                        ColDef.builder(Trade_.currentValue)
                                .build(),
                        ColDef.builder(Trade_.previousValue)
                                .build()
                )

                .treeData(true)
                .getChildCount(true)
                .getChildCountFieldName("childCount")
                .isServerSideGroupFieldName("hasChildren")
                .treeDataParentReferenceField(Trade_.parentTrade)
                .treeDataChildrenField(Trade_.childTrades)
                .treeDataDataPathFieldName(Trade_.dataPath)
                .treeDataDataPathSeparator("/")

                .build();

        this.aggregationTreeQueryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .enableValue(true)
                                .build(),
                        ColDef.builder(Trade_.currentValue)
                                .enableValue(true)
                                .build(),
                        ColDef.builder(Trade_.previousValue)
                                .enableValue(true)
                                .build()
                )

                .treeData(true)
                .isServerSideGroupFieldName("hasChildren")
                .treeDataParentReferenceField(Trade_.parentTrade)
                .treeDataChildrenField(Trade_.childTrades)
                .treeDataDataPathFieldName(Trade_.dataPath)
                .treeDataDataPathSeparator("/")

                .build();


        this.filteringQueryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        ColDef.builder(Trade_.product)
                                .filter(new AgTextColumnFilter())
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .filter(new AgTextColumnFilter())
                                .build(),
                        ColDef.builder(Trade_.dataPath)
                                .filter(new AgTextColumnFilter())
                                .build()
                )

                .treeData(true)
                .isServerSideGroupFieldName("hasChildren")
                .treeDataParentReferenceField(Trade_.parentTrade)
                .treeDataChildrenField(Trade_.childTrades)
                .treeDataDataPathFieldName(Trade_.dataPath)
                .treeDataDataPathSeparator("/")

                .build();

        this.filteringAllQueryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .build(),
                        ColDef.builder(Trade_.product)
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .build(),
                        ColDef.builder(Trade_.dataPath)
                                .build()
                )

                .enableAdvancedFilter(true)
                .isQuickFilterPresent(true)
                .quickFilterSearchInFields(
                        FieldPath.of(Trade_.product),
                        FieldPath.of(Trade_.portfolio),
                        FieldPath.of(Trade_.dataPath)
                )
                .isExternalFilterPresent(true)
                .doesExternalFilterPass((cb, root, externalFilterValue) -> {
                    if (externalFilterValue == null) {
                        return null;
                    }

                    String externalFilter = (String) externalFilterValue;
                    switch (externalFilter) {
                        case "Trade Id Odd" -> {
                            return cb.notEqual(cb.mod(root.get("tradeId"), 2), 0);
                        }
                        case "Trade Id Even" -> {
                            return cb.equal(cb.mod(root.get("tradeId"), 2), 0);
                        }
                        default -> {
                            return null;
                        }
                    }
                })

                .treeData(true)
                .isServerSideGroupFieldName("hasChildren")
                .treeDataParentReferenceField(Trade_.parentTrade)
                .treeDataChildrenField(Trade_.childTrades)
                .treeDataDataPathFieldName(Trade_.dataPath)
                .treeDataDataPathSeparator("/")

                .build();
    }


    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        return this.queryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getChildCountRows(ServerSideGetRowsRequest request) {
        return this.childCountQueryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getAggRows(ServerSideGetRowsRequest request) {
        return this.aggregationTreeQueryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getFilteredRows(ServerSideGetRowsRequest request) {
        return this.filteringQueryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getFilteredAllRows(ServerSideGetRowsRequest request) {
        return this.filteringAllQueryBuilder.getRows(request);
    }
}
