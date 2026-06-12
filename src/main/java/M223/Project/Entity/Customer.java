package M223.Project.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nachname;

    private String vorname;

    private String strasse;

    @Column(name = "plz_ort")
    private String plzOrt;

    private String telefon;

    private String email;

    // Ein Kunde kann mehrere Aufträge haben
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Work_Order> workOrders = new ArrayList<>();
}
