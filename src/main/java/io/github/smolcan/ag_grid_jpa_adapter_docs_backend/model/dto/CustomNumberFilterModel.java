package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto;

import io.github.smolcan.aggrid.jpa.adapter.filter.model.IFilterModel;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomNumberFilterModel implements IFilterModel {
    private String value;
}
