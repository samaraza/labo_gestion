package labo_gestion_api.repository;

import labo_gestion_api.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    boolean existsByReference(String reference);



    // ✅ طريقة جديدة: جلب المنتجات مع تحميل المورد (Fournisseur)
    @Query("SELECT p FROM Produit p LEFT JOIN FETCH p.fournisseur")
    List<Produit> findAllWithFournisseur();

}
