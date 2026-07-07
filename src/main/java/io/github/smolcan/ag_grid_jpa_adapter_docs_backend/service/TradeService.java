package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto.CustomNumberFilter;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto.CustomNumberFilterParams;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.*;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;
import io.github.smolcan.aggrid.jpa.adapter.column.FieldPath;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;


@Service
@Slf4j
public class TradeService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final EntityManager entityManager;
    private final QueryBuilder<Trade, Long, Void> queryBuilder;

    @Autowired
    public TradeService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        // trade id
                        ColDef.builder(Trade_.tradeId)
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgMultiColumnFilter<Long>()
                                        .filterParams(
                                                MultiFilterParams.<Long>builder()
                                                        .filters(
                                                                new AgNumberColumnFilter<>(),
                                                                new CustomNumberFilter<Long>()
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
                        ColDef.builder(Trade_.product)
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgMultiColumnFilter<String>()
                                        .filterParams(
                                                MultiFilterParams.<String>builder()
                                                        .filters(
                                                                new AgTextColumnFilter(),
                                                                AgSetColumnFilter.forString()
                                                                        .filterParams(
                                                                                SetFilterParams
                                                                                        .builder()
                                                                                        .textFormatter((cb, expr) -> cb.trim(cb.lower(expr)))
                                                                                        .build()
                                                                        )
                                                        )
                                                        .build()
                                        )
                                )
                                .build(),

                        // birthDate
                        ColDef.builder(Trade_.birthDate)
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgMultiColumnFilter<LocalDate>()
                                        .filterParams(
                                                MultiFilterParams.<LocalDate>builder()
                                                        .filters(
                                                                AgDateColumnFilter.forLocalDate(),
                                                                AgSetColumnFilter.forDate()
                                                        )
                                                        .build()
                                        )
                                )
                                .build(),

                        // isSold
                        ColDef.builder(Trade_.isSold)
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(AgSetColumnFilter.forBoolean())
                                .build(),

                        // Portfolio with text filter
                        ColDef.builder(Trade_.portfolio)
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
                        ColDef.builder(Trade_.book)
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        // Submitter ID with multi-column filter
                        ColDef.builder(FieldPath.of(Trade_.submitter).to(Submitter_.id))
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgMultiColumnFilter<Long>()
                                        .filterParams(
                                                MultiFilterParams.<Long>builder()
                                                        .filters(
                                                                new AgNumberColumnFilter<>(),
                                                                AgSetColumnFilter.forNumber()
                                                        )
                                                        .build()
                                        )
                                )
                                .build(),

                        // Submitter Deal ID with number filter
                        ColDef.builder(FieldPath.of(Trade_.submitterDeal).to(SubmitterDeal_.id))
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),

                        // Deal Type with text filter
                        ColDef.builder(Trade_.dealType)
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        // Bid Type with text filter
                        ColDef.builder(Trade_.bidType)
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgTextColumnFilter())
                                .build(),

                        // Current Value with number filter
                        ColDef.builder(Trade_.currentValue)
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),

                        // Previous Value with number filter
                        ColDef.builder(Trade_.previousValue)
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),

                        // PL1 with number filter
                        ColDef.builder(FieldPath.of(Trade_.statistics).to(Statistics_.pl1))
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),

                        // PL2 with number filter
                        ColDef.builder(FieldPath.of(Trade_.statistics).to(Statistics_.pl2))
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),

                        // Gain Dx with number filter
                        ColDef.builder(FieldPath.of(Trade_.statistics).to(Statistics_.gainDx))
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),

                        // SX Px with number filter
                        ColDef.builder(FieldPath.of(Trade_.statistics).to(Statistics_.sxPx))
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),

                        // X99 Out with number filter
                        ColDef.builder(FieldPath.of(Trade_.statistics).to(Statistics_.x99Out))
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),

                        // Batch with number filter
                        ColDef.builder(Trade_.batch)
                                .enableValue(true)
                                .enableRowGroup(true)
                                .enablePivot(true)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),

                        ColDef.builder(Trade_.dataPath)
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
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        log.info("getRows called, received request: ");
        log.info(OBJECT_MAPPER.writeValueAsString(request));
        log.info("executing...: ");
        return this.queryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public long countRows(ServerSideGetRowsRequest request) {
        log.info("countRows called, received request: ");
        log.info(OBJECT_MAPPER.writeValueAsString(request));
        log.info("executing...: ");
        return this.queryBuilder.countRows(request);
    }
}
