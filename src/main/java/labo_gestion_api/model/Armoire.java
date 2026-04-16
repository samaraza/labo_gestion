package labo_gestion_api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "armoires")
public class Armoire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "designation", nullable = false, length = 100)
    private String designation;

    // العلاقة مع SalleTp (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_tp_id")
    @JsonIgnore
    private SalleTp salleTp;

    // العلاقة مع Produit (One-to-Many)
    @OneToMany(mappedBy = "armoire",  orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Produit> produits = new ArrayList<>();

    // ✅ Helper methods - تم التصحيح
    public void addProduit(Produit produit) {
        if (produit != null) {
            produits.add(produit);
            produit.setArmoire(this);
        }
    }

    // ✅ تصحيح: تغيير اسم المتغير من produitCommande إلى produit
    public void removeProduit(Produit produit) {
        if (produit != null) {
            produits.remove(produit);
            produit.setArmoire(null);
        }
    }

    public void clearProduits() {
        produits.forEach(produit -> produit.setArmoire(null));
        produits.clear();
    }
}