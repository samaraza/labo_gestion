package labo_gestion_api.repository;

import labo_gestion_api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import labo_gestion_api.model.RoleEnum;
import java.util.Optional;

public interface RoleRepository  extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleEnum role);
}
