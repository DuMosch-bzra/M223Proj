package ch.glauserillnau.auftragsverwaltung.controller;

import ch.glauserillnau.auftragsverwaltung.dto.ReportDto;
import ch.glauserillnau.auftragsverwaltung.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/work-orders/{workOrderId}/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<ReportDto.Response> get(@PathVariable Long workOrderId) {
        return ResponseEntity.ok(reportService.findByWorkOrderId(workOrderId));
    }

    @PostMapping
    public ResponseEntity<ReportDto.Response> create(
            @PathVariable Long workOrderId,
            @Valid @RequestBody ReportDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.create(workOrderId, request));
    }

    @PatchMapping("/approve")
    public ResponseEntity<ReportDto.Response> approve(@PathVariable Long workOrderId) {
        return ResponseEntity.ok(reportService.approve(workOrderId));
    }
}
