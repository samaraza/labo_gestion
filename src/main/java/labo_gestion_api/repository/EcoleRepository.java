package labo_gestion_api.repository;

import labo_gestion_api.model.Commande;
import labo_gestion_api.model.Ecole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EcoleRepository  extends JpaRepository<Ecole, Long> {



    // ✅ البحث بالمدرسة بالمعرف
    @Query("SELECT e FROM Ecole e WHERE e.id = :schoolId")
    Optional<Ecole> findById(@Param("schoolId") Integer schoolId);

    // ✅ البحث بالمدرسة بالاسم
    Optional<Ecole> findByName(String name);

}
