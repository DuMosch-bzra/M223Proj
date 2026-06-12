package M223.Project.Repository;

import M223.Project.Entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Rapport zu einem bestimmten Auftrag
    Optional<Report> findByOrderId(Long orderId);

    // Alle noch nicht freigegebenen Rapporte (für BL)
    List<Report> findByFreigegebenFalse();
}