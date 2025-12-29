package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto;

import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper<T> {
    
    private T data;
    private List<String> sql;
    
}
