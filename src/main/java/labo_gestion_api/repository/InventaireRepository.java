package labo_gestion_api.repository;

import labo_gestion_api.model.Inventaire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventaireRepository extends JpaRepository<Inventaire, Long> {
}
