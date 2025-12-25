package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.controller.docs;

import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.docs.AggregationService;
import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/docs/aggregation")
@RequiredArgsConstructor
public class AggregationController {

    private final AggregationService aggregationService;

    @PostMapping("getRows")
    public ResponseEntity<LoadSuccessParams> getRows(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.aggregationService.getRows(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/suppress-agg-filtered-only/getRows")
    public ResponseEntity<LoadSuccessParams> getRowsSuppressAggFilteredOnly(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.aggregationService.getRowsSuppressAggFilteredOnly(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/group-agg-filtering/getRows")
    public ResponseEntity<LoadSuccessParams> getRowsGroupAggFiltering(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.aggregationService.getRowsGroupAggFiltering(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/custom-agg-func/getRows")
    public ResponseEntity<LoadSuccessParams> getRowsCustomAggFunc(@RequestBody ServerSideGetRowsRequest request) {
        LoadSuccessParams result = this.aggregationService.getRowsCustomAggFunc(request);
        return ResponseEntity.ok(result);
    }

}
