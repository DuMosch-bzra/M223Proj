package M223.Project.Entity;


import M223.Project.Entity.enums.DocumentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Work_Order workOrder;

    @NotBlank
    @Column(nullable = false)
    private String dateiname;           // z.B. "auftragsblatt_A-2025-041.pdf"

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType typ;

    @Column(name = "hochgeladen_am", nullable = false)
    @Builder.Default
    private LocalDateTime hochgeladenAm = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hochgeladen_von")
    private Employe hochgeladenVon;

    @Column(name = "datei_pfad", nullable = false)
    private String dateiPfad;           // Pfad auf dem Server oder URL

    @Column(name = "dateigrösse")
    private Long dateigroesse;          // Bytes
}
