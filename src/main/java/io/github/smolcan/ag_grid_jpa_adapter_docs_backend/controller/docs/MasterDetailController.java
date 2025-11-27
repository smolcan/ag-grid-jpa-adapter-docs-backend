package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.controller.docs;


import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs.MasterDetailService;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/docs/master-detail")
@RequiredArgsConstructor
public class MasterDetailController {
    
    private final MasterDetailService masterDetailService;

    @PostMapping("getRows")
    public ResponseEntity<LoadSuccessParams> getRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.masterDetailService.getRows(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("getDetailRowData")
    public ResponseEntity<List<Map<String, Object>>> getDetailRowData(@RequestBody Map<String, Object> masterRow) {
        List<Map<String, Object>> result = this.masterDetailService.getDetailRowData(masterRow);
        return ResponseEntity.ok(result);
    }
    
}
