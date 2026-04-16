package labo_gestion_api.repository;

import labo_gestion_api.model.Preparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PreparationRepository extends JpaRepository<Preparation, Long> {

    @Query("SELECT p FROM Preparation p " +
            "LEFT JOIN FETCH p.produit1 " +
            "LEFT JOIN FETCH p.produit2")
    List<Preparation> findAllWithProduits();
}
