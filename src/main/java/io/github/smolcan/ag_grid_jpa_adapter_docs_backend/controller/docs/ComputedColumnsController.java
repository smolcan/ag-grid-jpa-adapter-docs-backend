package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.controller.docs;


import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs.ComputedColumnsService;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/docs/computed-columns")
@RequiredArgsConstructor
public class ComputedColumnsController {

    private final ComputedColumnsService computedColumnsService;

    @PostMapping("getRows")
    public ResponseEntity<LoadSuccessParams> getRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.computedColumnsService.getRows(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("supplySetFilterValues/{field}")
    public ResponseEntity<List<Object>> supplySetFilterValues(@PathVariable String field) {
        return ResponseEntity.ok(this.computedColumnsService.supplySetFilterValues(field));
    }

}
