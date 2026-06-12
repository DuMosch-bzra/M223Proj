package M223.Project.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Work_Order workOrder;

    @Column(nullable = false, unique = true)
    private String rechnungsnummer;     // z.B. "R-2025-041"

    @Column(name = "rechnungsdatum")
    private LocalDate rechnungsdatum;

    @Column(name = "betrag_netto", precision = 10, scale = 2)
    private BigDecimal betragNetto;

    @Column(name = "mwst_satz", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal mwstSatz = new BigDecimal("8.1"); // CH MwSt

    @Column(name = "betrag_brutto", precision = 10, scale = 2)
    private BigDecimal betragBrutto;

    @Column(length = 1000)
    private String bemerkungen;

    @Column(name = "bezahlt")
    @Builder.Default
    private boolean bezahlt = false;

    @Column(name = "bezahlt_am")
    private LocalDate bezahltAm;
}
