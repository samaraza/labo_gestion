package labo_gestion_api.repository;

import labo_gestion_api.model.Labo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaboRepository extends JpaRepository<Labo, Long> {
}
