package labo_gestion_api.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonFormat;

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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "commandes")
@EntityListeners(AuditingEntityListener.class)
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "designation", nullable = false, length = 200)
    private String designation;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "observation", length = 500)
    private String observation;

    @Column(name = "numero", unique = true, nullable = false, length = 50)
    private String numero;

    // العلاقة مع Fournisseur (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;

    // العلاقة مع User (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // ✅ العلاقة مع ProduitCommande (One-to-Many) - تم التصحيح
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProduitCommande> produits = new ArrayList<>();

    // ✅ أضف هذا - ربط الأمر بمدرسة
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ecole_id")
    private Ecole ecole;

    // Dates d'audit
    @CreatedDate
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    // ✅ Helper methods - تم التصحيح (إزالة com.example.gestionlabo.model.)
    public void addProduit(ProduitCommande produitCommande) {
        if (produitCommande != null) {
            produits.add(produitCommande);
            produitCommande.setCommande(this);
        }
    }

    public void removeProduit(ProduitCommande produitCommande) {
        if (produitCommande != null) {
            produits.remove(produitCommande);
            produitCommande.setCommande(null);
        }
    }

    public void clearProduits() {
        produits.forEach(produit -> produit.setCommande(null));
        produits.clear();
    }
}