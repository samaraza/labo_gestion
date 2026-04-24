package labo_gestion_api.repository;

import labo_gestion_api.model.Commande;
import labo_gestion_api.model.Ecole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EcoleRepository  extends JpaRepository<Ecole, Long> {


    // ✅ تصحيح: تغيير Optional<Object> إلى Optional<Ecole>
    @Query("SELECT e FROM Ecole e WHERE e.id = :schoolId")
    Optional<Ecole> findById(@Param("schoolId") Integer schoolId);

    // ✅ إضافة دالة للبحث بالاسم
    Optional<Ecole> findByName(String name);

    // ✅ جلب كل المدارس مع المستخدمين (اختياري)
    @Query("SELECT DISTINCT e FROM Ecole e LEFT JOIN FETCH e.users")
    List<Ecole> findAllWithUsers();
}
