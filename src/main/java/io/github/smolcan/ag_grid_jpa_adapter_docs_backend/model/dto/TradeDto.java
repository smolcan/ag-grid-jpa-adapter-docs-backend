package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Statistics;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity.Trade;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
public class TradeDto {
    private Long tradeId;
    private String product;
    private String portfolio;
    private String book;
    private Long submitterId;
    private Long submitterDealId;
    private String dealType;
    private String bidType;
    private BigDecimal currentValue;
    private BigDecimal previousValue;
    private Statistics statistics;
    private Integer batch;
    private LocalDate birthDate;
    @JsonProperty("isSold")
    private boolean isSold;
    private String dataPath;
    
    public TradeDto(Trade trade) {
        this.tradeId = trade.getTradeId();
        this.product = trade.getProduct();
        this.portfolio = trade.getPortfolio();
        this.book = trade.getBook();
        this.submitterId = trade.getSubmitter() != null ? trade.getSubmitter().getId() : null;
        this.submitterDealId = trade.getSubmitterDeal() != null ? trade.getSubmitterDeal().getId() : null;
        this.dealType = trade.getDealType();
        this.bidType = trade.getBidType();
        this.currentValue = trade.getCurrentValue();
        this.previousValue = trade.getPreviousValue();
        this.statistics = trade.getStatistics();
        this.batch = trade.getBatch();
        this.birthDate = trade.getBirthDate();
        this.isSold = trade.getIsSold();
        this.dataPath = trade.getDataPath();
    }
}
