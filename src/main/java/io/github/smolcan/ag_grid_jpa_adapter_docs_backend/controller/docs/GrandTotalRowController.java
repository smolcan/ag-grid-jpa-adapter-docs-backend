package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.controller.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs.GrandTotalRowService;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/docs/grand-total-row")
@RequiredArgsConstructor
public class GrandTotalRowController {
    
    private final GrandTotalRowService grandTotalRowService;
    
    @PostMapping("getRows")
    public ResponseEntity<LoadSuccessParams> getRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.grandTotalRowService.getRows(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("supplySetFilterValues/{field}")
    public ResponseEntity<List<Object>> supplySetFilterValues(@PathVariable String field) {
        return ResponseEntity.ok(this.grandTotalRowService.supplySetFilterValues(field));
    }


    @PostMapping("async/getRows")
    public ResponseEntity<LoadSuccessParams> getRowsAsync(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.grandTotalRowService.getRowsAsync(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("async/supplySetFilterValues/{field}")
    public ResponseEntity<List<Object>> supplySetFilterValuesAsync(@PathVariable String field) {
        return ResponseEntity.ok(this.grandTotalRowService.supplySetFilterValuesAsync(field));
    }

    @PostMapping("async/getGrandTotalData")
    public ResponseEntity<Map<String, Object>> getGrandTotalDataAsync(@RequestBody ServerSideGetRowsRequest request) {
        Map<String, Object> result = this.grandTotalRowService.getGrandTotalDataAsync(request);
        return ResponseEntity.ok(result);
    }

}
