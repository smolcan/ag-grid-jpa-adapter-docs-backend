package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "trade")
@Getter @Setter
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // For auto-increment behavior
    @Column(name = "trade_id")
    private Long tradeId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_trade_id")
    private Trade parentTrade;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentTrade", orphanRemoval = true)
    private Set<Trade> childTrades = new HashSet<>();

    @Column(name = "product", length = 255)
    private String product;

    @Column(name = "portfolio", length = 255)
    private String portfolio;

    @Column(name = "book", length = 255)
    private String book;

    @Column(name = "submitter_id")
    private Integer submitterId;

    @Column(name = "submitter_deal_id")
    private Integer submitterDealId;

    @Column(name = "deal_type", length = 255)
    private String dealType;

    @Column(name = "bid_type", length = 255)
    private String bidType;

    @Column(name = "current_value")
    private BigDecimal currentValue;

    @Column(name = "previous_value")
    private BigDecimal previousValue;

    @Column(name = "pl1")
    private BigDecimal pl1;

    @Column(name = "pl2")
    private BigDecimal pl2;

    @Column(name = "gain_dx")
    private BigDecimal gainDx;

    @Column(name = "sx_px")
    private BigDecimal sxPx;

    @Column(name = "x99_out")
    private BigDecimal x99Out;

    @Column(name = "batch")
    private Integer batch;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "is_sold")
    private Boolean isSold;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Trade trade && this.tradeId.equals(trade.tradeId);
    }

    @Override
    public int hashCode() {
        return this.tradeId.hashCode();
    }
}
