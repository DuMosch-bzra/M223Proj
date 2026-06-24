package ch.glauserillnau.auftragsverwaltung.repository;

import ch.glauserillnau.auftragsverwaltung.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByWorkOrderId(Long workOrderId);
}
