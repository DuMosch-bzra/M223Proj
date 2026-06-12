package M223.Project.Repository;

import M223.Project.Entity.Document;
import M223.Project.Entity.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByOrderId(Long orderId);

    List<Document> findByOrderIdAndTyp(Long orderId, DocumentType typ);
}