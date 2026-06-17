package ch.glauserillnau.auftragsverwaltung.repository;

import ch.glauserillnau.auftragsverwaltung.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByWorkOrderId(Long workOrderId);
    boolean existsByWorkOrderId(Long workOrderId);
}
