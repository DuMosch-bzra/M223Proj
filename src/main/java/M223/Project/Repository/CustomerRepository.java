package M223.Project.Repository;

import M223.Project.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByNachnameContainingIgnoreCase(String nachname);

    boolean existsByEmailIgnoreCase(String email);
}