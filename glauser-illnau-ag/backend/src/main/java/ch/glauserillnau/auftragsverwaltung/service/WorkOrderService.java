package ch.glauserillnau.auftragsverwaltung.service;

import ch.glauserillnau.auftragsverwaltung.dto.WorkOrderDto;
import ch.glauserillnau.auftragsverwaltung.entity.Customer;
import ch.glauserillnau.auftragsverwaltung.entity.Employee;
import ch.glauserillnau.auftragsverwaltung.entity.WorkOrder;
import ch.glauserillnau.auftragsverwaltung.enums.OrderStatus;
import ch.glauserillnau.auftragsverwaltung.repository.CustomerRepository;
import ch.glauserillnau.auftragsverwaltung.repository.EmployeeRepository;
import ch.glauserillnau.auftragsverwaltung.repository.WorkOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    // ── CREATE ────────────────────────────────────────────────────────────────

    public WorkOrderDto.Response create(WorkOrderDto.CreateRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Kunde nicht gefunden: " + request.getCustomerId()));

        WorkOrder order = WorkOrder.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .address(request.getAddress())
                .customer(customer)
                .status(OrderStatus.CREATED)
                .build();

        return toResponse(workOrderRepository.save(order));
    }

    // ── READ ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<WorkOrderDto.Response> findAll() {
        return workOrderRepository.findAllWithDetails().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WorkOrderDto.Response> findByStatus(OrderStatus status) {
        return workOrderRepository.findByStatusWithDetails(status).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkOrderDto.Response findById(Long id) {
        return toResponse(getOrder(id));
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    public WorkOrderDto.Response update(Long id, WorkOrderDto.UpdateRequest request) {
        WorkOrder order = getOrder(id);
        if (request.getTitle() != null) order.setTitle(request.getTitle());
        if (request.getDescription() != null) order.setDescription(request.getDescription());
        if (request.getAddress() != null) order.setAddress(request.getAddress());
        return toResponse(workOrderRepository.save(order));
    }

    // ── STATUS TRANSITIONS ────────────────────────────────────────────────────

    /** Schritt 2: BL disponiert – weist Mitarbeiter zu, setzt Termin */
    public WorkOrderDto.Response dispatch(Long id, WorkOrderDto.DispatchRequest request) {
        WorkOrder order = getOrder(id);
        assertStatus(order, OrderStatus.CREATED, "Nur CREATED-Aufträge können disponiert werden");

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Mitarbeiter nicht gefunden: " + request.getEmployeeId()));

        order.setEmployee(employee);
        order.setStatus(OrderStatus.SCHEDULED);
        if (request.getScheduledAt() != null) {
            order.setScheduledAt(request.getScheduledAt());
        }
        return toResponse(workOrderRepository.save(order));
    }

    /** Schritt 3a: MA startet Ausführung */
    public WorkOrderDto.Response markInProgress(Long id) {
        WorkOrder order = getOrder(id);
        assertStatus(order, OrderStatus.SCHEDULED, "Nur SCHEDULED-Aufträge können gestartet werden");
        order.setStatus(OrderStatus.IN_PROGRESS);
        return toResponse(workOrderRepository.save(order));
    }

    /** Schritt 3b: MA schliesst ab und rapportiert */
    public WorkOrderDto.Response markCompleted(Long id) {
        WorkOrder order = getOrder(id);
        assertStatus(order, OrderStatus.IN_PROGRESS, "Nur IN_PROGRESS-Aufträge können abgeschlossen werden");
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        return toResponse(workOrderRepository.save(order));
    }

    /** Schritt 5: BL gibt Rapport zur Verrechnung frei */
    public WorkOrderDto.Response approveReport(Long id) {
        WorkOrder order = getOrder(id);
        assertStatus(order, OrderStatus.COMPLETED, "Nur COMPLETED-Aufträge können freigegeben werden");
        order.setStatus(OrderStatus.APPROVED);
        return toResponse(workOrderRepository.save(order));
    }

    /** Schritt 6: Admin verrechnet */
    public WorkOrderDto.Response markInvoiced(Long id) {
        WorkOrder order = getOrder(id);
        assertStatus(order, OrderStatus.APPROVED, "Nur APPROVED-Aufträge können verrechnet werden");
        order.setStatus(OrderStatus.INVOICED);
        order.setInvoicedAt(LocalDateTime.now());
        return toResponse(workOrderRepository.save(order));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    public void delete(Long id) {
        if (!workOrderRepository.existsById(id)) {
            throw new EntityNotFoundException("Auftrag nicht gefunden: " + id);
        }
        workOrderRepository.deleteById(id);
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private WorkOrder getOrder(Long id) {
        return workOrderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Auftrag nicht gefunden: " + id));
    }

    private void assertStatus(WorkOrder order, OrderStatus expected, String message) {
        if (order.getStatus() != expected) {
            throw new IllegalStateException(message + " (aktuell: " + order.getStatus() + ")");
        }
    }

    public WorkOrderDto.Response toResponse(WorkOrder order) {
        WorkOrderDto.Response dto = new WorkOrderDto.Response();
        dto.setId(order.getId());
        dto.setTitle(order.getTitle());
        dto.setDescription(order.getDescription());
        dto.setAddress(order.getAddress());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setScheduledAt(order.getScheduledAt());
        dto.setCompletedAt(order.getCompletedAt());
        dto.setInvoicedAt(order.getInvoicedAt());

        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getId());
            dto.setCustomerName(order.getCustomer().getCompanyName());
        }
        if (order.getEmployee() != null) {
            dto.setEmployeeId(order.getEmployee().getId());
            dto.setEmployeeName(order.getEmployee().getFullName());
        }
        return dto;
    }
}
