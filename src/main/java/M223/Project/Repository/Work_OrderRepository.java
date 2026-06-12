package M223.Project.Repository;

import M223.Project.Entity.Work_Order;
import M223.Project.Entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Work_OrderRepository extends JpaRepository<Work_Order, Long> {


    List<Work_Order> findByStatus(OrderStatus status);

    List<Work_Order> findByMitarbeiterId(Long mitarbeiterId);

    List<Work_Order> findByCustomerId(Long customerId);

    Optional<Work_Order> findByAuftragsnummer(String auftragsnummer);

    List<Work_Order> findByStatusOrderByDatumDesc(OrderStatus status);
}