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

    @PostMapping("eager/getRows")
    public ResponseEntity<LoadSuccessParams> getEagerRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.masterDetailService.getEagerRows(request);
        return ResponseEntity.ok(result);
    }


    @PostMapping("dynamic/getRows")
    public ResponseEntity<LoadSuccessParams> getDynamicRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.masterDetailService.getDynamicRows(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("dynamic/getDetailRowData")
    public ResponseEntity<List<Map<String, Object>>> getDynamicDetailRowData(@RequestBody Map<String, Object> masterRow) {
        List<Map<String, Object>> result = this.masterDetailService.getDynamicDetailRowData(masterRow);
        return ResponseEntity.ok(result);
    }

    @PostMapping("tree/getRows")
    public ResponseEntity<LoadSuccessParams> getTreeDataRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.masterDetailService.getTreeDataRows(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("tree/getDetailRowData")
    public ResponseEntity<List<Map<String, Object>>> getTreeDataDetailRowData(@RequestBody Map<String, Object> masterRow) {
        List<Map<String, Object>> result = this.masterDetailService.getTreeDataDetailRowData(masterRow);
        return ResponseEntity.ok(result);
    }
    
}
