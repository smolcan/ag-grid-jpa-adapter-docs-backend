package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.dto.TradeDto;
import io.github.smolcan.ag_grid_jpa_adapter_docs_backend.service.TradeService;

import io.github.smolcan.aggrid.jpa.adapter.request.ServerSideGetRowsRequest;
import io.github.smolcan.aggrid.jpa.adapter.response.LoadSuccessParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class TradeController {
    
    private final TradeService tradeService;
    
    @GetMapping("/getClientSideRows")
    public ResponseEntity<List<TradeDto>> getRowsForClientSideModel() {
        List<TradeDto> res = this.tradeService.getRowsForClientSideModel()
                .stream()
                .map(TradeDto::new)
                .toList();
        return ResponseEntity.ok(res);
    }
    
    @PostMapping("/getRows")
    public ResponseEntity<LoadSuccessParams> getRows(@RequestBody ServerSideGetRowsRequest request) throws JsonProcessingException {
        LoadSuccessParams result = this.tradeService.getRows(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/countRows")
    public ResponseEntity<Long> countRows(@RequestBody ServerSideGetRowsRequest request) throws JsonProcessingException {
        long result = this.tradeService.countRows(request);
        return ResponseEntity.ok(result);
    }
    
}
