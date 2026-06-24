package ch.glauserillnau.auftragsverwaltung.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @Column(name = "contact_person", length = 255)
    private String contactPerson;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<WorkOrder> workOrders = new ArrayList<>();
}
