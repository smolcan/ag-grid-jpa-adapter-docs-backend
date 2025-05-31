package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Integer submitterId;
    private Integer submitterDealId;
    private String dealType;
    private String bidType;
    private BigDecimal currentValue;
    private BigDecimal previousValue;
    private BigDecimal pl1;
    private BigDecimal pl2;
    private BigDecimal gainDx;
    private BigDecimal sxPx;
    private BigDecimal x99Out;
    private Integer batch;
    private LocalDate birthDate;
    @JsonProperty("isSold")
    private boolean isSold;
    
    public TradeDto(Trade trade) {
        this.tradeId = trade.getTradeId();
        this.product = trade.getProduct();
        this.portfolio = trade.getPortfolio();
        this.book = trade.getBook();
        this.submitterId = trade.getSubmitterId();
        this.submitterDealId = trade.getSubmitterDealId();
        this.dealType = trade.getDealType();
        this.bidType = trade.getBidType();
        this.currentValue = trade.getCurrentValue();
        this.previousValue = trade.getPreviousValue();
        this.pl1 = trade.getPl1();
        this.pl2 = trade.getPl2();
        this.gainDx = trade.getGainDx();
        this.sxPx = trade.getSxPx();
        this.x99Out = trade.getX99Out();
        this.batch = trade.getBatch();
        this.birthDate = trade.getBirthDate();
        this.isSold = trade.getIsSold();
    }
}
