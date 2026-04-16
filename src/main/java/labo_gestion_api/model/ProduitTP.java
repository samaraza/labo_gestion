package labo_gestion_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "produits_tp")
public class ProduitTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    // ✅ إضافة العلاقة مع Tp (مهم جداً)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tp_id")
    @JsonIgnore
    private Tp travailPratique;

    // ✅ إضافة setter و getter (Lombok سيقوم بتوليدها تلقائياً مع @Data)
    // ولكن يمكن إضافتها يدوياً للتأكد
    public void setTravailPratique(Tp tp) {
        this.travailPratique = tp;
    }

    public Tp getTravailPratique() {
        return travailPratique;
    }
}