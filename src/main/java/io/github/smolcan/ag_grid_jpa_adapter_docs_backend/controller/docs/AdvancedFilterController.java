package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.controller.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs.AdvancedFilterService;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/docs/filtering/advanced-filter")
@RequiredArgsConstructor
public class AdvancedFilterController {

    private final AdvancedFilterService advancedFilterService;

    @PostMapping("getRows")
    public ResponseEntity<LoadSuccessParams> getRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.advancedFilterService.getRows(request);
        return ResponseEntity.ok(result);
    }
    
}
