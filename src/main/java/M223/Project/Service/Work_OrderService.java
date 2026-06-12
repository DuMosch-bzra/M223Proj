package M223.Project.Service;
import M223.Project.Entity.Work_Order;
import M223.Project.Repository.Work_OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Work_OrderService {
    @Autowired
    private Work_OrderRepository work_orderRepository;
    public List<Work_Order> getAllNoten() {
        return work_orderRepository.findAll();
    }
}
