package labo_gestion_api.repository;

import labo_gestion_api.model.Armoire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArmoireRepository extends JpaRepository<Armoire, Long> {
}
