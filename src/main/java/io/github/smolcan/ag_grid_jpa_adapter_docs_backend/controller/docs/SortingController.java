package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.controller.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs.SortingService;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/docs/sorting")
@RequiredArgsConstructor
public class SortingController {
    
    private final SortingService sortingService;

    @PostMapping("getRows")
    public ResponseEntity<LoadSuccessParams> getRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.sortingService.getRows(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("absolute/getRows")
    public ResponseEntity<LoadSuccessParams> getAbsoluteRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.sortingService.getAbsoluteRows(request);
        return ResponseEntity.ok(result);
    }
    
    
}
