package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto.CustomNumberFilter;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto.CustomNumberFilterParams;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;

import io.github.smolcan.aggrid.jpa.adapter.filter.model.simple.params.MultiFilterParams;
import io.github.smolcan.aggrid.jpa.adapter.filter.model.simple.params.SetFilterParams;
import io.github.smolcan.aggrid.jpa.adapter.filter.model.simple.params.TextFilterParams;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.AgMultiColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.AgSetColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgDateColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgNumberColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgTextColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;


@Service
public class TradeService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(TradeService.class.getName());
    
    private final EntityManager entityManager;
    private final QueryBuilder<Trade> queryBuilder;
    
    @Autowired
    public TradeService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryBuilder = QueryBuilder.builder(Trade.class, entityManager)
                .colDefs(
                        // trade id
                        ColDef.builder()
                                .field("tradeId")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgMultiColumnFilter()
                                        
                                        .filterParams(
                                                MultiFilterParams.builder()
                                                        .filters(
                                                                new AgNumberColumnFilter(),
                                                                new CustomNumberFilter()
                                                                        .filterParams(
                                                                                CustomNumberFilterParams
                                                                                        .builder()
                                                                                        .includeNullValues(true)
                                                                                        .build()
                                                                        )
                                                        )
                                                        .build()
                                        )
                                )
                                .build(),
                        
                        // product
                        ColDef.builder()
                                .field("product")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgMultiColumnFilter()
                                        .filterParams(
                                                MultiFilterParams.builder()
                                                        .filters(
                                                                new AgTextColumnFilter(),
                                                                new AgSetColumnFilter()
                                                                        .filterParams(
                                                                                SetFilterParams
                                                                                        .builder()
                                                                                        .textFormatter((cb, expr) -> {
                                                                                            return cb.trim(cb.lower(expr));
                                                                                        })
                                                                                        .build()
                                                                        )
                                                        )
                                                        .build()
                                        )
                                )
                                .build(),
                        
                        // birthDate
                        ColDef.builder()
                                .field("birthDate")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgMultiColumnFilter()
                                        .filterParams(
                                                MultiFilterParams.builder()
                                                        .filters(
                                                                new AgDateColumnFilter(),
                                                                new AgSetColumnFilter()
                                                        )
                                                        .build()
                                        )
                                )
                                .build(),
                        
                        // isSold
                        ColDef.builder()
                                .field("isSold")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgSetColumnFilter())
                                .build(),

                        // Portfolio with text filter
                        ColDef.builder()
                                .field("portfolio")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
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
                                                                            cb.literal("áéíóúÁÉÍÓÚüÜñÑ"),
                                                                            cb.literal("aeiouAEIOUuUnN"));
                                                                    
                                                                    return newExpression;
                                                                })
                                                                .build()
                                                )
                                )
                                .build(),

                        // Book with text filter
                        ColDef.builder()
                                .field("book")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        // Submitter ID with multi-column filter
                        ColDef.builder()
                                .field("submitter.id")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgMultiColumnFilter()
                                        .filterParams(
                                                MultiFilterParams.builder()
                                                        .filters(
                                                                new AgNumberColumnFilter(),
                                                                new AgSetColumnFilter()
                                                        )
                                                        .build()
                                        )
                                )
                                .build(),

                        // Submitter Deal ID with number filter
                        ColDef.builder()
                                .field("submitterDeal.id")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),

                        // Deal Type with text filter
                        ColDef.builder()
                                .field("dealType")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        // Bid Type with text filter
                        ColDef.builder()
                                .field("bidType")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        // Current Value with number filter
                        ColDef.builder()
                                .field("currentValue")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),

                        // Previous Value with number filter
                        ColDef.builder()
                                .field("previousValue")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),

                        // PL1 with number filter
                        ColDef.builder()
                                .field("pl1")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),

                        // PL2 with number filter
                        ColDef.builder()
                                .field("pl2")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),

                        // Gain Dx with number filter
                        ColDef.builder()
                                .field("gainDx")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),

                        // SX Px with number filter
                        ColDef.builder()
                                .field("sxPx")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),

                        // X99 Out with number filter
                        ColDef.builder()
                                .field("x99Out")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),

                        // Batch with number filter
                        ColDef.builder()
                                .field("batch")
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter())
                                .build(),

                        ColDef.builder()
                                .field("dataPath")
                                .build()

                        )
                .enableAdvancedFilter(true)
                .paginateChildRows(true)
                .build();
    }
    
    public List<Trade> getRowsForClientSideModel() {
        return this.entityManager.createQuery("SELECT t FROM Trade t", Trade.class)
                .setMaxResults(50000)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) throws JsonProcessingException {
        LOGGER.info("getRows called, received request: ");
        LOGGER.info(OBJECT_MAPPER.writeValueAsString(request));
        LOGGER.info("executing...: ");
        return this.queryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public long countRows(ServerSideGetRowsRequest request)
            throws JsonProcessingException {
        LOGGER.info("countRows called, received request: ");
        LOGGER.info(OBJECT_MAPPER.writeValueAsString(request));
        LOGGER.info("executing...: ");
        return this.queryBuilder.countRows(request);
    }
}
