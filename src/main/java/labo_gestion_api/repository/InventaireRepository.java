package labo_gestion_api.repository;

import labo_gestion_api.model.Inventaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventaireRepository extends JpaRepository<Inventaire, Long> {


    // ✅ جلب جميع المخزونات لمدرسة محددة
    @Query("SELECT i FROM Inventaire i WHERE i.ecole.id = :ecoleId")
    List<Inventaire> findByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب المخزونات مع المنتجات لمدرسة محددة
    @Query("SELECT i FROM Inventaire i " +
            "LEFT JOIN FETCH i.produit " +
            "WHERE i.ecole.id = :ecoleId")
    List<Inventaire> findInventairesWithProduitsByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب مخزون محدد والتأكد أنه ينتمي للمدرسة
    @Query("SELECT i FROM Inventaire i WHERE i.id = :id AND i.ecole.id = :ecoleId")
    Inventaire findByIdAndEcoleId(@Param("id") Long id, @Param("ecoleId") Integer ecoleId);

    // ✅ جلب المخزون حسب سنة دراسية لمدرسة محددة
    @Query("SELECT i FROM Inventaire i WHERE i.anneeScolaire = :anneeScolaire AND i.ecole.id = :ecoleId")
    List<Inventaire> findByAnneeScolaireAndEcoleId(@Param("anneeScolaire") String anneeScolaire, @Param("ecoleId") Integer ecoleId);
}
