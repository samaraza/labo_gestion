package labo_gestion_api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tps")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private TpType type;

    @Column(name = "jour_tp", length = 50)
    private String jourTp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prof_id")
    private User prof;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_tp_id")
    private SalleTp salleTp;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveau_scolaire", length = 50)
    private NiveauScolaire niveauScolaire;

    @OneToMany(mappedBy = "tp", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PreparationTP> preparations = new ArrayList<>();

    @OneToMany(mappedBy = "travailPratique", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProduitTP> produits = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    // Helper methods
    public void addPreparation(PreparationTP preparation) {
        preparations.add(preparation);
        preparation.setTp(this);
    }

    public void removePreparation(PreparationTP preparation) {
        preparations.remove(preparation);
        preparation.setTp(null);
    }

    public void addProduit(ProduitTP produit) {
        produits.add(produit);
        produit.setTravailPratique(this);
    }

    public void removeProduit(ProduitTP produit) {
        produits.remove(produit);
        produit.setTravailPratique(null);
    }

    public void clearPreparations() {
        preparations.forEach(prep -> prep.setTp(null));
        preparations.clear();
    }

    public void clearProduits() {
        produits.forEach(prod -> prod.setTravailPratique(null));
        produits.clear();
    }




    // ✅ أضف هذا
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ecole_id")
    private Ecole ecole;
}