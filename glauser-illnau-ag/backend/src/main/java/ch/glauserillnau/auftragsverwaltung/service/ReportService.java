package ch.glauserillnau.auftragsverwaltung.service;

import ch.glauserillnau.auftragsverwaltung.dto.ReportDto;
import ch.glauserillnau.auftragsverwaltung.entity.Report;
import ch.glauserillnau.auftragsverwaltung.entity.WorkOrder;
import ch.glauserillnau.auftragsverwaltung.enums.OrderStatus;
import ch.glauserillnau.auftragsverwaltung.repository.ReportRepository;
import ch.glauserillnau.auftragsverwaltung.repository.WorkOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final WorkOrderRepository workOrderRepository;

    /** MA erfasst Rapport – Auftrag wird automatisch auf COMPLETED gesetzt */
    public ReportDto.Response create(Long workOrderId, ReportDto.CreateRequest request) {
        WorkOrder order = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Auftrag nicht gefunden: " + workOrderId));

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                "Rapport kann nur für Aufträge im Status IN_PROGRESS erstellt werden (aktuell: " + order.getStatus() + ")"
            );
        }
        if (reportRepository.existsByWorkOrderId(workOrderId)) {
            throw new IllegalStateException("Für diesen Auftrag existiert bereits ein Rapport");
        }

        Report report = Report.builder()
                .workDescription(request.getWorkDescription())
                .workingHours(request.getWorkingHours())
                .usedMaterials(request.getUsedMaterials())
                .workOrder(order)
                .approved(false)
                .build();

        reportRepository.save(report);

        // Auto-advance order status to COMPLETED
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(java.time.LocalDateTime.now());
        workOrderRepository.save(order);

        return toResponse(report);
    }

    @Transactional(readOnly = true)
    public ReportDto.Response findByWorkOrderId(Long workOrderId) {
        return toResponse(reportRepository.findByWorkOrderId(workOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Kein Rapport für Auftrag: " + workOrderId)));
    }

    /** BL genehmigt Rapport */
    public ReportDto.Response approve(Long workOrderId) {
        Report report = reportRepository.findByWorkOrderId(workOrderId)
                .orElseThrow(() -> new EntityNotFoundException("Kein Rapport für Auftrag: " + workOrderId));
        report.setApproved(true);
        return toResponse(reportRepository.save(report));
    }

    private ReportDto.Response toResponse(Report r) {
        ReportDto.Response dto = new ReportDto.Response();
        dto.setId(r.getId());
        dto.setWorkDescription(r.getWorkDescription());
        dto.setWorkingHours(r.getWorkingHours());
        dto.setUsedMaterials(r.getUsedMaterials());
        dto.setApproved(r.getApproved());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setWorkOrderId(r.getWorkOrder().getId());
        return dto;
    }
}
