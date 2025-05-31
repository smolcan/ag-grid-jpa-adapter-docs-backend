package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.controller.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs.PaginationService;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/docs/pagination")
@RequiredArgsConstructor
public class PaginationController {
    
    private final PaginationService paginationService;

    @PostMapping("getRows")
    public ResponseEntity<LoadSuccessParams> getRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.paginationService.getRows(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/countRows")
    public ResponseEntity<Long> countRows(@RequestBody ServerSideGetRowsRequest request) {
        long result = this.paginationService.countRows(request);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("paginate-child-rows/getRows")
    public ResponseEntity<LoadSuccessParams> paginateChildRowsGetRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.paginationService.paginateChildRowsGetRows(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("paginate-child-rows/countRows")
    public ResponseEntity<Long> paginateChildRowsCountRows(@RequestBody ServerSideGetRowsRequest request) {
        long result = this.paginationService.paginateChildRowsCountRows(request);
        return ResponseEntity.ok(result);
    }
}
