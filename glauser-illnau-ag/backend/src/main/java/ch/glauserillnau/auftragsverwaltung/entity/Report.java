package ch.glauserillnau.auftragsverwaltung.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_description", columnDefinition = "TEXT")
    private String workDescription;

    @Column(name = "working_hours", columnDefinition = "DECIMAL(5,2)")
    private Double workingHours;

    @Column(name = "used_materials", columnDefinition = "TEXT")
    private String usedMaterials;

    @Column(name = "approved", nullable = false)
    @Builder.Default
    private Boolean approved = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private WorkOrder workOrder;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
