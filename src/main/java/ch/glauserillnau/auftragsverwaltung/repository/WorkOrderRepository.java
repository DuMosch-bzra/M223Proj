package ch.glauserillnau.auftragsverwaltung.repository;

import ch.glauserillnau.auftragsverwaltung.entity.WorkOrder;
import ch.glauserillnau.auftragsverwaltung.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    List<WorkOrder> findByStatus(OrderStatus status);

    List<WorkOrder> findByEmployeeId(Long employeeId);

    List<WorkOrder> findByCustomerId(Long customerId);

    @Query("SELECT w FROM WorkOrder w LEFT JOIN FETCH w.customer LEFT JOIN FETCH w.employee ORDER BY w.createdAt DESC")
    List<WorkOrder> findAllWithDetails();

    List<WorkOrder> findByStatusOrderByCreatedAtDesc(OrderStatus status);
}
