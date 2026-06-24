package ch.glauserillnau.auftragsverwaltung.repository;

import ch.glauserillnau.auftragsverwaltung.entity.WorkOrder;
import ch.glauserillnau.auftragsverwaltung.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    // Eagerly fetch customer + employee to avoid N+1 and LazyInitializationException
    @Query("SELECT w FROM WorkOrder w LEFT JOIN FETCH w.customer LEFT JOIN FETCH w.employee ORDER BY w.createdAt DESC")
    List<WorkOrder> findAllWithDetails();

    @Query("SELECT w FROM WorkOrder w LEFT JOIN FETCH w.customer LEFT JOIN FETCH w.employee WHERE w.status = :status ORDER BY w.createdAt DESC")
    List<WorkOrder> findByStatusWithDetails(OrderStatus status);

    @Query("SELECT w FROM WorkOrder w LEFT JOIN FETCH w.customer LEFT JOIN FETCH w.employee WHERE w.id = :id")
    Optional<WorkOrder> findByIdWithDetails(Long id);

    List<WorkOrder> findByEmployeeId(Long employeeId);
    List<WorkOrder> findByCustomerId(Long customerId);
}
