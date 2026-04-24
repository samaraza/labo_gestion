package labo_gestion_api.repository;

import labo_gestion_api.model.Labo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LaboRepository extends JpaRepository<Labo, Long> {

    // ✅ جلب جميع المختبرات لمدرسة محددة
    @Query("SELECT l FROM Labo l WHERE l.ecole.id = :ecoleId")
    List<Labo> findByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب المختبرات مع القاعات لمدرسة محددة
    @Query("SELECT DISTINCT l FROM Labo l " +
            "LEFT JOIN FETCH l.salleTps " +
            "WHERE l.ecole.id = :ecoleId")
    List<Labo> findLabosWithSallesByEcoleId(@Param("ecoleId") Integer ecoleId);
}
