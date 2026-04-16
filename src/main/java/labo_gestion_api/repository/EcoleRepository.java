package labo_gestion_api.repository;

import labo_gestion_api.model.Commande;
import labo_gestion_api.model.Ecole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EcoleRepository  extends JpaRepository<Ecole, Long> {
    Optional<Object> findById(Integer schoolId);
}
