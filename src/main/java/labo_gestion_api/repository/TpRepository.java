package labo_gestion_api.repository;

import labo_gestion_api.model.Tp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TpRepository extends JpaRepository<Tp, Long> {

    @Query("SELECT DISTINCT t FROM Tp t " +
            "LEFT JOIN FETCH t.preparations p " +
            "LEFT JOIN FETCH p.preparation " +
            "LEFT JOIN FETCH t.produits pr " +
            "LEFT JOIN FETCH pr.produit")
    List<Tp> findAllWithDetails();
}