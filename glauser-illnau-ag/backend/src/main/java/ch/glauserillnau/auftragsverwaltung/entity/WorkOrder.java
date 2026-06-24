package ch.glauserillnau.auftragsverwaltung.entity;

import ch.glauserillnau.auftragsverwaltung.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "work_order") // maps to work_order table (renamed from "order" to avoid SQL keyword)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "address", length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "invoiced_at")
    private LocalDateTime invoicedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // ---- Relations ----

    @OneToOne(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Report report;

    @OneToOne(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Invoice invoice;

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    // ---- Lifecycle ----

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
