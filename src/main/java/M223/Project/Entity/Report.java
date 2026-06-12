package M223.Project.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "reports")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Work_Order workOrder;

    @Column(name = "ausführungsdatum")
    private LocalDate ausfuehrungsdatum;

    @Column(name = "arbeitsstunden")
    private Double arbeitsstunden;

    @Column(length = 500)
    private String material;

    @Column(length = 2000)
    private String bemerkungen;


    @Column(name = "freigegeben")
    @Builder.Default
    private boolean freigegeben = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "erstellt_von")
    private Employe erstelltVon;
}
