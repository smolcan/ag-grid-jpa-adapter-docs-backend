package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.AbstractEntity_;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade_;
import io.github.smolcan.aggrid.jpa.adapter.column.ColDef;

import io.github.smolcan.aggrid.jpa.adapter.column.FieldPath;
import io.github.smolcan.aggrid.jpa.adapter.filter.model.advanced.ColumnAdvancedFilterModel;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.AgSetColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgDateColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgNumberColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.filter.provided.simple.AgTextColumnFilter;
import io.github.smolcan.aggrid.jpa.adapter.query.QueryBuilder;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AdvancedFilterService {

    private final QueryBuilder<Trade, Long, Void> queryBuilder;
    private final QueryBuilder<Trade, Long, Void> customFilterOptionsQueryBuilder;

    @Autowired
    public AdvancedFilterService(EntityManager entityManager) {
        this.queryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        // strings
                        ColDef.builder(Trade_.product)
                                .filter(AgSetColumnFilter.forString())
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .filter(new AgTextColumnFilter())
                                .build(),
                        ColDef.builder(Trade_.book)
                                .filter(new AgTextColumnFilter())
                                .build(),
                        // numbers
                        ColDef.builder(FieldPath.of(Trade_.submitter).to(AbstractEntity_.id))
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        ColDef.builder(FieldPath.of(Trade_.submitterDeal).to(AbstractEntity_.id))
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        // date
                        ColDef.builder(Trade_.birthDate)
                                .filter(AgDateColumnFilter.forLocalDate())
                                .build(),
                        // boolean
                        ColDef.builder(Trade_.isSold)
                                .filter(AgSetColumnFilter.forBoolean())
                                .build()
                )
                .enableAdvancedFilter(true)
                .build();

        this.customFilterOptionsQueryBuilder = QueryBuilder.builder(Trade.class, Trade_.tradeId, entityManager)
                .colDefs(
                        ColDef.builder(Trade_.tradeId)
                                .build(),
                        ColDef.builder(Trade_.portfolio)
                                .filter(new AgTextColumnFilter())
                                .build(),
                        ColDef.builder(Trade_.currentValue)
                                .filter(new AgNumberColumnFilter<>())
                                .build(),
                        ColDef.builder(Trade_.birthDate)
                                .filter(AgDateColumnFilter.forLocalDate())
                                .build()
                )
                .enableAdvancedFilter(true)
                // registered under the option's displayKey
                .registerCustomAdvancedFilter("startsWithVowel", _ -> new StartsWithVowelFilterModel())
                .registerCustomAdvancedFilter("betweenExclusive", BetweenExclusiveFilterModel::new)
                .registerCustomAdvancedFilter("sameYearAs", SameYearAsFilterModel::new)
                .build();
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRows(ServerSideGetRowsRequest request) {
        return this.queryBuilder.getRows(request);
    }

    @Transactional(readOnly = true)
    public List<Object> supplySetFilterValues(String field) {
        return this.queryBuilder.supplySetFilterValues(field);
    }

    @Transactional(readOnly = true)
    public LoadSuccessParams getRowsCustomFilterOptions(ServerSideGetRowsRequest request) {
        return this.customFilterOptionsQueryBuilder.getRows(request);
    }


    /**
     * Portfolio starts with a vowel, takes no input.
     */
    private static class StartsWithVowelFilterModel extends ColumnAdvancedFilterModel<Trade, String> {

        StartsWithVowelFilterModel() {
            super("text", FieldPath.of(Trade_.portfolio));
        }

        @Override
        @NonNull
        public Predicate toPredicate(CriteriaBuilder cb, @NonNull Root<? extends Trade> root) {
            Expression<String> portfolio = cb.lower(this.getColumnField().getExpression(cb, root));
            return cb.or(
                    cb.like(portfolio, "a%"),
                    cb.like(portfolio, "e%"),
                    cb.like(portfolio, "i%"),
                    cb.like(portfolio, "o%"),
                    cb.like(portfolio, "u%")
            );
        }
    }

    /**
     * Current value strictly between two inputs.
     */
    private static class BetweenExclusiveFilterModel extends ColumnAdvancedFilterModel<Trade, BigDecimal> {

        private final BigDecimal from;
        private final BigDecimal to;

        BetweenExclusiveFilterModel(Map<String, Object> filter) {
            super("number", FieldPath.of(Trade_.currentValue));
            // numbers arrive as JSON numbers
            this.from = new BigDecimal(filter.get("filter").toString());
            this.to = new BigDecimal(filter.get("filterTo").toString());
        }

        @Override
        @NonNull
        public Predicate toPredicate(@NonNull CriteriaBuilder cb, @NonNull Root<? extends Trade> root) {
            Expression<BigDecimal> currentValue = this.getColumnField().getExpression(cb, root);
            return cb.and(cb.gt(currentValue, this.from), cb.lt(currentValue, this.to));
        }
    }

    /**
     * Birth date in the same year as the input.
     */
    private static class SameYearAsFilterModel extends ColumnAdvancedFilterModel<Trade, LocalDate> {

        private final LocalDate startOfYear;

        SameYearAsFilterModel(Map<String, Object> filter) {
            super("dateString", FieldPath.of(Trade_.birthDate));
            // dates arrive as yyyy-MM-dd
            this.startOfYear = LocalDate.parse(filter.get("filter").toString()).withDayOfYear(1);
        }

        @Override
        @NonNull
        public Predicate toPredicate(@NonNull CriteriaBuilder cb, @NonNull Root<? extends Trade> root) {
            Expression<LocalDate> birthDate = this.getColumnField().getExpression(cb, root);
            return cb.and(
                    cb.greaterThanOrEqualTo(birthDate, this.startOfYear),
                    cb.lessThan(birthDate, this.startOfYear.plusYears(1))
            );
        }
    }
}
