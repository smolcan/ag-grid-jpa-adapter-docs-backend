package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.controller.docs;


import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs.DotNotationService;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/docs/dot-notation")
@RequiredArgsConstructor
public class DotNotationController {
    
    private final DotNotationService dotNotationService;

    @PostMapping("getRows")
    public ResponseEntity<LoadSuccessParams> getRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.dotNotationService.getRows(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("flat-data/getRows")
    public ResponseEntity<LoadSuccessParams> getFlatDataRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.dotNotationService.getFlatDataRows(request);
        return ResponseEntity.ok(result);
    }
    
}
