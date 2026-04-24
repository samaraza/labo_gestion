package labo_gestion_api.repository;

import labo_gestion_api.model.SalleTp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalleTpRepository extends JpaRepository<SalleTp, Long> {
    boolean existsByNumero(String numero);



    // ✅ جلب جميع القاعات لمدرسة محددة
    @Query("SELECT s FROM SalleTp s WHERE s.ecole.id = :ecoleId")
    List<SalleTp> findByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب القاعات مع الخزائن لمدرسة محددة
    @Query("SELECT DISTINCT s FROM SalleTp s " +
            "LEFT JOIN FETCH s.armoires " +
            "WHERE s.ecole.id = :ecoleId")
    List<SalleTp> findSallesWithArmoiresByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب قاعة محددة والتأكد أنها تنتمي للمدرسة
    @Query("SELECT s FROM SalleTp s WHERE s.id = :id AND s.ecole.id = :ecoleId")
    SalleTp findByIdAndEcoleId(@Param("id") Long id, @Param("ecoleId") Integer ecoleId);

    // ✅ التحقق من وجود رقم قاعة لمدرسة محددة
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SalleTp s " +
            "WHERE s.numero = :numero AND s.ecole.id = :ecoleId")
    boolean existsByNumeroAndEcoleId(@Param("numero") String numero, @Param("ecoleId") Integer ecoleId);

}
