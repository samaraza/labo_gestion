package labo_gestion_api.repository;

import labo_gestion_api.model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommandeRepository  extends JpaRepository<Commande, Long> {

    @Query("SELECT c FROM Commande c LEFT JOIN FETCH c.user LEFT JOIN FETCH c.fournisseur")
    List<Commande> findAllWithUsersAndFournisseurs();

    // ✅ جلب جميع الطلبيات لمدرسة محددة
    @Query("SELECT c FROM Commande c WHERE c.ecole.id = :ecoleId")
    List<Commande> findByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب الطلبيات مع التفاصيل لمدرسة محددة
    @Query("SELECT c FROM Commande c " +
            "LEFT JOIN FETCH c.user " +
            "LEFT JOIN FETCH c.fournisseur " +
            "WHERE c.ecole.id = :ecoleId")
    List<Commande> findAllWithUsersAndFournisseursByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ جلب أمر محدد والتأكد أنه ينتمي للمدرسة
    @Query("SELECT c FROM Commande c WHERE c.id = :id AND c.ecole.id = :ecoleId")
    Commande findByIdAndEcoleId(@Param("id") Long id, @Param("ecoleId") Integer ecoleId);


}
