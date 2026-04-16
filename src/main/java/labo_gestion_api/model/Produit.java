package labo_gestion_api.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "produits")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "designation", nullable = false, length = 200)
    private String designation;

    @Column(name = "reference", unique = true, nullable = false, length = 50)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private ProduitType type;

    @Column(name = "date_expiration")
    private LocalDate dateExp;

    @Enumerated(EnumType.STRING)
    @Column(name = "categorie", nullable = false, length = 50)
    private Categorie categorie;

    @Enumerated(EnumType.STRING)
    @Column(name = "rubrique", nullable = false, length = 50)
    private Rubrique rubrique;

    @Enumerated(EnumType.STRING)
    @Column(name = "durabilite", length = 50)
    private Durabilite durabilite;

    @Column(name = "quantite_initial", nullable = false)
    private Integer quantiteInitiale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Fournisseur fournisseur;

    @Enumerated(EnumType.STRING)
    @Column(name = "unite_mesure", nullable = false, length = 20)
    private UniteMesure uniteMesure;

    // ✅ إضافة العلاقة مع Armoire
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "armoire_id")
    @JsonIgnore
    private Armoire armoire;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Transient   // يعني لا تحفظه في قاعدة البيانات
    private Long fournisseurId;

    // ✅ إضافة Getters و Setters يدوياً (إذا لم يعمل Lombok)
    public void setArmoire(Armoire armoire) {
        this.armoire = armoire;
    }

    public Armoire getArmoire() {
        return armoire;
    }
}