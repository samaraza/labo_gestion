package labo_gestion_api.repository;

import labo_gestion_api.model.RoleEnum;
import labo_gestion_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);


    // ✅ أضف هذه الدالة الجديدة:
    @Query("SELECT u FROM User u WHERE u.ecole.id = :ecoleId")
    List<User> findByEcoleId(@Param("ecoleId") Integer ecoleId);

    // ✅ وأضف هذه أيضاً للمستخدمين حسب الدور (PROFFESSEUR, PREPARATEUR)
    @Query("SELECT u FROM User u WHERE u.ecole.id = :ecoleId AND u.schoolRole = :role")
    List<User> findByEcoleIdAndSchoolRole(@Param("ecoleId") Integer ecoleId, @Param("role") RoleEnum role);
}