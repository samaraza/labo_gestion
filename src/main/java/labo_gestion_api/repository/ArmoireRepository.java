package labo_gestion_api.repository;

import labo_gestion_api.model.Armoire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArmoireRepository extends JpaRepository<Armoire, Long> {


    // ✅ جلب جميع الخزائن لمدرسة محددة
    @Query("SELECT a FROM Armoire a WHERE a.ecole.id = :ecoleId")
    List<Armoire> findByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب الخزائن مع المنتجات لمدرسة محددة
    @Query("SELECT DISTINCT a FROM Armoire a " +
            "LEFT JOIN FETCH a.produits " +
            "WHERE a.ecole.id = :ecoleId")
    List<Armoire> findArmoiresWithProduitsByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب خزانة محددة والتأكد أنها تنتمي للمدرسة
    @Query("SELECT a FROM Armoire a WHERE a.id = :id AND a.ecole.id = :ecoleId")
    Armoire findByIdAndEcoleId(@Param("id") Long id, @Param("ecoleId") Integer ecoleId);
}
