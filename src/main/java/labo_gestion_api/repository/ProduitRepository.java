package labo_gestion_api.repository;

import labo_gestion_api.model.Categorie;
import labo_gestion_api.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    boolean existsByReference(String reference);

    // ✅ طريقة جديدة: جلب المنتجات مع تحميل المورد (Fournisseur)
    @Query("SELECT p FROM Produit p LEFT JOIN FETCH p.fournisseur")
    List<Produit> findAllWithFournisseur();


    // ✅ جلب جميع المنتجات لمدرسة محددة
    @Query("SELECT p FROM Produit p WHERE p.ecole.id = :ecoleId")
    List<Produit> findByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب المنتجات مع المورد لمدرسة محددة
    @Query("SELECT p FROM Produit p LEFT JOIN FETCH p.fournisseur WHERE p.ecole.id = :ecoleId")
    List<Produit> findAllWithFournisseurByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب منتج محدد والتأكد أنه ينتمي للمدرسة
    @Query("SELECT p FROM Produit p WHERE p.id = :id AND p.ecole.id = :ecoleId")
    Produit findByIdAndEcoleId(@Param("id") Long id, @Param("ecoleId") Integer ecoleId);

    // ✅ البحث عن منتجات حسب التصنيف لمدرسة محددة
    @Query("SELECT p FROM Produit p WHERE p.categorie = :categorie AND p.ecole.id = :ecoleId")
    List<Produit> findByCategorieAndEcoleId(@Param("categorie") Categorie categorie, @Param("ecoleId") Integer ecoleId);

}
