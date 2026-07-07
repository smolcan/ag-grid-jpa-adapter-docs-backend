package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;


import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade_;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;

import io.github.smolcan.aggrid.jpa.adapter.filter.model.simple.params.DateFilterParams;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgDateColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static io.github.smolcan.aggrid.jpa.adapter.filter.model.simple.SimpleFilterModelType.*;

@Service
public class DateFilterService {

    private final QueryBuilder<Trade, Long, Void> queryBuilder;
    private final QueryBuilder<Trade, Long, Void> relativeDateQueryBuilder;

    @Autowired
    public DateFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .build(),
                        
                        ColDef.builder(Trade_.birthDate)
                                .filter(
                                        AgDateColumnFilter.forLocalDate()
                                                .filterParams(
                                                        DateFilterParams.builder()
                                                                .inRangeInclusive(true)
                                                                .includeBlanksInEquals(true)
                                                                .includeBlanksInNotEqual(true)
                                                                .includeBlanksInLessThan(true)
                                                                .includeBlanksInGreaterThan(true)
                                                                .includeBlanksInRange(true)
                                                                .maxValidYear(LocalDate.now().getYear() + 1)
                                                                .minValidYear(LocalDate.now().getYear() - 1)
                                                                .build()
                                                )
                                )
                                .build()
                )
                .build();

        this.relativeDateQueryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .build(),
                        ColDef.builder(Trade_.birthDate)
                                .filter(
                                        AgDateColumnFilter.forLocalDate()
                                                .filterParams(
                                                        DateFilterParams.builder()
                                                                .filterOptions(
                                                                        today,
                                                                        yesterday,
                                                                        tomorrow,
                                                                        thisWeek,
                                                                        lastWeek,
                                                                        // using next week will throw exception, not listed
//                                                                        nextWeek,
                                                                        thisMonth,
                                                                        lastMonth,
                                                                        nextMonth,
                                                                        thisQuarter,
                                                                        lastQuarter,
                                                                        nextQuarter,
                                                                        thisYear,
                                                                        lastYear,
                                                                        nextYear,
                                                                        yearToDate,
                                                                        last7Days,
                                                                        last30Days,
                                                                        last90Days,
                                                                        last6Months,
                                                                        last12Months,
                                                                        last24Months
                                                                )
                                                                .build()
                                                )
                                )
                                .build()
                )
                .build();
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        return this.queryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRelativeRows(ServerSideGetRowsRequest request) {
        return this.relativeDateQueryBuilder.getRows(request);
    }
}
