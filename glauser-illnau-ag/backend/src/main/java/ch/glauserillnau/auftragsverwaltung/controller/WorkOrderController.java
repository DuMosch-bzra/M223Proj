package ch.glauserillnau.auftragsverwaltung.controller;

import ch.glauserillnau.auftragsverwaltung.dto.WorkOrderDto;
import ch.glauserillnau.auftragsverwaltung.enums.OrderStatus;
import ch.glauserillnau.auftragsverwaltung.service.WorkOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    /** GET /api/work-orders               – alle Aufträge */
    @GetMapping
    public ResponseEntity<List<WorkOrderDto.Response>> getAll(
            @RequestParam(required = false) OrderStatus status) {
        if (status != null) {
            return ResponseEntity.ok(workOrderService.findByStatus(status));
        }
        return ResponseEntity.ok(workOrderService.findAll());
    }

    /** GET /api/work-orders/{id}          – Einzelauftrag */
    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.findById(id));
    }

    /** POST /api/work-orders              – Auftrag erfassen (GL/Admin) */
    @PostMapping
    public ResponseEntity<WorkOrderDto.Response> create(
            @Valid @RequestBody WorkOrderDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workOrderService.create(request));
    }

    /** PUT /api/work-orders/{id}          – Auftrag aktualisieren */
    @PutMapping("/{id}")
    public ResponseEntity<WorkOrderDto.Response> update(
            @PathVariable Long id,
            @RequestBody WorkOrderDto.UpdateRequest request) {
        return ResponseEntity.ok(workOrderService.update(id, request));
    }

    /** PATCH /api/work-orders/{id}/dispatch – Disponieren (BL) */
    @PatchMapping("/{id}/dispatch")
    public ResponseEntity<WorkOrderDto.Response> dispatch(
            @PathVariable Long id,
            @Valid @RequestBody WorkOrderDto.DispatchRequest request) {
        return ResponseEntity.ok(workOrderService.dispatch(id, request));
    }

    /** PATCH /api/work-orders/{id}/start  – Auftrag starten (MA) */
    @PatchMapping("/{id}/start")
    public ResponseEntity<WorkOrderDto.Response> start(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.markInProgress(id));
    }

    /** PATCH /api/work-orders/{id}/complete – Auftrag abschliessen (MA) */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<WorkOrderDto.Response> complete(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.markCompleted(id));
    }

    /** PATCH /api/work-orders/{id}/approve-report – Rapport freigeben (BL) */
    @PatchMapping("/{id}/approve-report")
    public ResponseEntity<WorkOrderDto.Response> approveReport(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.approveReport(id));
    }

    /** PATCH /api/work-orders/{id}/invoice – Auftrag verrechnen (Admin) */
    @PatchMapping("/{id}/invoice")
    public ResponseEntity<WorkOrderDto.Response> invoice(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.markInvoiced(id));
    }

    /** DELETE /api/work-orders/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workOrderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
