package labo_gestion_api.repository;

import labo_gestion_api.model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommandeRepository  extends JpaRepository<Commande, Long> {

    @Query("SELECT c FROM Commande c LEFT JOIN FETCH c.user LEFT JOIN FETCH c.fournisseur")
    List<Commande> findAllWithUsersAndFournisseurs();
}
