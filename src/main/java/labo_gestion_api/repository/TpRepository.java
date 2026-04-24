package labo_gestion_api.repository;

import labo_gestion_api.model.Tp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TpRepository extends JpaRepository<Tp, Long> {

    @Query("SELECT DISTINCT t FROM Tp t " +
            "LEFT JOIN FETCH t.preparations p " +
            "LEFT JOIN FETCH p.preparation " +
            "LEFT JOIN FETCH t.produits pr " +
            "LEFT JOIN FETCH pr.produit")
    List<Tp> findAllWithDetails();


    // ✅ جلب جميع TPs لمدرسة محددة
    @Query("SELECT t FROM Tp t WHERE t.ecole.id = :ecoleId")
    List<Tp> findByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب TPs مع التفاصيل لمدرسة محددة
    @Query("SELECT DISTINCT t FROM Tp t " +
            "LEFT JOIN FETCH t.preparations p " +
            "LEFT JOIN FETCH p.preparation " +
            "LEFT JOIN FETCH t.produits pr " +
            "LEFT JOIN FETCH pr.produit " +
            "WHERE t.ecole.id = :ecoleId")
    List<Tp> findAllWithDetailsByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب TP محدد والتأكد أنه ينتمي للمدرسة
    @Query("SELECT t FROM Tp t WHERE t.id = :id AND t.ecole.id = :ecoleId")
    Tp findByIdAndEcoleId(@Param("id") Long id, @Param("ecoleId") Integer ecoleId);

    // ✅ جلب TPs حسب الأستاذ (prof) لمدرسة محددة
    @Query("SELECT t FROM Tp t WHERE t.prof.id = :profId AND t.ecole.id = :ecoleId")
    List<Tp> findByProfIdAndEcoleId(@Param("profId") Integer profId, @Param("ecoleId") Integer ecoleId);
}