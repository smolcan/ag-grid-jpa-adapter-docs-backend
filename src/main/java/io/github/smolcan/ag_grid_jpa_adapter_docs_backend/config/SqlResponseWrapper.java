package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.config;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto.ResponseWrapper;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


import java.util.List;

@ControllerAdvice
public class SqlResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // We only care if the return type inside ResponseEntity is LoadSuccessParams
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        List<String> queries = SqlQueryContext.getQueries();
        ResponseWrapper<?> finalResponse = new ResponseWrapper<>(body, queries);
        SqlQueryContext.clear();

        return finalResponse;
    }
}