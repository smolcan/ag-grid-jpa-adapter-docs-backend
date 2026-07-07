package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record Statistics(
        @Column(name = "pl1")
        BigDecimal pl1,

        @Column(name = "pl2")
        BigDecimal pl2,

        @Column(name = "gain_dx")
        BigDecimal gainDx,

        @Column(name = "sx_px")
        BigDecimal sxPx,

        @Column(name = "x99_out")
        BigDecimal x99Out
) {
}
