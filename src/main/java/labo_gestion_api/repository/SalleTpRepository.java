package labo_gestion_api.repository;

import labo_gestion_api.model.SalleTp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalleTpRepository extends JpaRepository<SalleTp, Long> {
    boolean existsByNumero(String numero);

}
