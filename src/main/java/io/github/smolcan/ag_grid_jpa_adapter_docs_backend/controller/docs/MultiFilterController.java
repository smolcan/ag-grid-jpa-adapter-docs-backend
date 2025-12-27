package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.controller.docs;


import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs.MultiFilterService;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/docs/filtering/column-filter/multi-filter")
@RequiredArgsConstructor
public class MultiFilterController {

    private final MultiFilterService multiFilterService;

    @PostMapping("getRows")
    public ResponseEntity<LoadSuccessParams> getRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.multiFilterService.getRows(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("supplySetFilterValues/{field}")
    public ResponseEntity<List<Object>> supplySetFilterValues(@PathVariable String field) {
        return ResponseEntity.ok(this.multiFilterService.supplySetFilterValues(field));
    }
}
