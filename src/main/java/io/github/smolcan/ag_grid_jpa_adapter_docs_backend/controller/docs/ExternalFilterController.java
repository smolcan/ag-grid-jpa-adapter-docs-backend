package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.controller.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs.ExternalFilterService;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/docs/filtering/external-filter")
@RequiredArgsConstructor
public class ExternalFilterController {
    
    private final ExternalFilterService externalFilterService;

    @PostMapping("getRows")
    public ResponseEntity<LoadSuccessParams> getRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.externalFilterService.getRows(request);
        return ResponseEntity.ok(result);
    }
    
}
