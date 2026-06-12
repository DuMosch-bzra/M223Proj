package M223.Project.Repository;

import M223.Project.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByOrderId(Long orderId);

    List<Appointment> findByMitarbeiterId(Long mitarbeiterId);
}