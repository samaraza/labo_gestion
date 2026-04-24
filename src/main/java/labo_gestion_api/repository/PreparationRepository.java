package labo_gestion_api.repository;

import labo_gestion_api.model.Preparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PreparationRepository extends JpaRepository<Preparation, Long> {

    @Query("SELECT p FROM Preparation p " +
            "LEFT JOIN FETCH p.produit1 " +
            "LEFT JOIN FETCH p.produit2")
    List<Preparation> findAllWithProduits();

    // ✅ جلب جميع التحضيرات لمدرسة محددة
    @Query("SELECT p FROM Preparation p WHERE p.ecole.id = :ecoleId")
    List<Preparation> findByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب التحضيرات مع المنتجات لمدرسة محددة
    @Query("SELECT p FROM Preparation p " +
            "LEFT JOIN FETCH p.produit1 " +
            "LEFT JOIN FETCH p.produit2 " +
            "WHERE p.ecole.id = :ecoleId")
    List<Preparation> findAllWithProduitsByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب تحضير محدد والتأكد أنه ينتمي للمدرسة
    @Query("SELECT p FROM Preparation p WHERE p.id = :id AND p.ecole.id = :ecoleId")
    Preparation findByIdAndEcoleId(@Param("id") Long id, @Param("ecoleId") Integer ecoleId);
}
