package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto;

import io.github.smolcan.aggrid.jpa.adapter.filter.model.simple.params.IFilterParams;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class CustomNumberFilterParams implements IFilterParams {
    private boolean includeNullValues;
}
