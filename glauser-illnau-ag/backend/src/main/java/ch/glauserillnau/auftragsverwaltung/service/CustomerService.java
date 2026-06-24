package ch.glauserillnau.auftragsverwaltung.service;

import ch.glauserillnau.auftragsverwaltung.dto.CustomerDto;
import ch.glauserillnau.auftragsverwaltung.entity.Customer;
import ch.glauserillnau.auftragsverwaltung.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerDto.Response create(CustomerDto.CreateRequest request) {
        Customer customer = Customer.builder()
                .companyName(request.getCompanyName())
                .contactPerson(request.getContactPerson())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .address(request.getAddress())
                .build();
        return toResponse(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public List<CustomerDto.Response> findAll() {
        return customerRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CustomerDto.Response findById(Long id) {
        return toResponse(customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kunde nicht gefunden: " + id)));
    }

    public CustomerDto.Response update(Long id, CustomerDto.CreateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kunde nicht gefunden: " + id));
        customer.setCompanyName(request.getCompanyName());
        customer.setContactPerson(request.getContactPerson());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        return toResponse(customerRepository.save(customer));
    }

    private CustomerDto.Response toResponse(Customer c) {
        CustomerDto.Response dto = new CustomerDto.Response();
        dto.setId(c.getId());
        dto.setCompanyName(c.getCompanyName());
        dto.setContactPerson(c.getContactPerson());
        dto.setPhoneNumber(c.getPhoneNumber());
        dto.setEmail(c.getEmail());
        dto.setAddress(c.getAddress());
        return dto;
    }
}
