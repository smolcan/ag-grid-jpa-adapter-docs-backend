package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;

import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgNumberColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgTextColumnFilter;
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
public class TreeDataService {

    private final QueryBuilder<Trade> queryBuilder;
    private final QueryBuilder<Trade> childCountQueryBuilder;
    private final QueryBuilder<Trade> aggregationTreeQueryBuilder;
    private final QueryBuilder<Trade> filteringQueryBuilder;
    private final QueryBuilder<Trade> filteringAllQueryBuilder;

    @Autowired
    public TreeDataService(EntityManager entityManager) {

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
                                .build(),
                        ColDef.builder()
                                .field("currentValue")
                                .build(),
                        ColDef.builder()
                                .field("previousValue")
                                .build()
                )

                .treeData(true)
                .primaryFieldName("tradeId")
                .isServerSideGroupFieldName("hasChildren")
                .treeDataParentReferenceField("parentTrade")
                .treeDataChildrenField("childTrades")

                .build();

        this.childCountQueryBuilder = QueryBuilder.builder(Trade.class, entityManager)
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
                                .field("currentValue")
                                .build(),
                        ColDef.builder()
                                .field("previousValue")
                                .build()
                )

                .treeData(true)
                .getChildCount(true)
                .getChildCountFieldName("childCount")
                .primaryFieldName("tradeId")
                .isServerSideGroupFieldName("hasChildren")
                .treeDataParentReferenceField("parentTrade")
                .treeDataChildrenField("childTrades")
                .treeDataDataPathFieldName("dataPath")
                .treeDataDataPathSeparator("/")

                .build();

        this.aggregationTreeQueryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .enableValue(true)
                                .build(),
                        ColDef.builder()
                                .field("currentValue")
                                .enableValue(true)
                                .build(),
                        ColDef.builder()
                                .field("previousValue")
                                .enableValue(true)
                                .build()
                )

                .treeData(true)
                .primaryFieldName("tradeId")
                .isServerSideGroupFieldName("hasChildren")
                .treeDataParentReferenceField("parentTrade")
                .treeDataChildrenField("childTrades")
                .treeDataDataPathFieldName("dataPath")
                .treeDataDataPathSeparator("/")

                .build();


        this.filteringQueryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        ColDef.builder()
                                .field("tradeId")
                                .filter(new AgNumberColumnFilter())
                                .build(),
                        ColDef.builder()
                                .field("product")
                                .filter(new AgTextColumnFilter())
                                .build(),
                        ColDef.builder()
                                .field("portfolio")
                                .filter(new AgTextColumnFilter())
                                .build(),
                        ColDef.builder()
                                .field("dataPath")
                                .filter(new AgTextColumnFilter())
                                .build()
                )

                .treeData(true)
                .primaryFieldName("tradeId")
                .isServerSideGroupFieldName("hasChildren")
                .treeDataParentReferenceField("parentTrade")
                .treeDataChildrenField("childTrades")
                .treeDataDataPathFieldName("dataPath")
                .treeDataDataPathSeparator("/")

                .build();

        this.filteringAllQueryBuilder = QueryBuilder.builder(Trade.class, entityManager)
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
                                .field("dataPath")
                                .build()
                )
                
                .enableAdvancedFilter(true)
                .isQuickFilterPresent(true)
                .quickFilterSearchInFields("tradeId", "product", "portfolio", "dataPath")
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
                .primaryFieldName("tradeId")
                .isServerSideGroupFieldName("hasChildren")
                .treeDataParentReferenceField("parentTrade")
                .treeDataChildrenField("childTrades")
                .treeDataDataPathFieldName("dataPath")
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
