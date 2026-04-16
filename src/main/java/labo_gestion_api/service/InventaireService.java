package labo_gestion_api.service;

import labo_gestion_api.model.Inventaire;
import labo_gestion_api.model.Produit;
import labo_gestion_api.repository.InventaireRepository;
import labo_gestion_api.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InventaireService {

    @Autowired
    private InventaireRepository inventaireRepository;

    @Autowired
    private ProduitRepository produitRepository;

    public List<Inventaire> findAll() {
        return inventaireRepository.findAll();
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public Inventaire findById(Long id) {
        return inventaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventaire not found with id " + id));
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public Inventaire save(Inventaire inventaire) {
        if (inventaire.getProduit() == null || inventaire.getProduit().getId() == null) {
            throw new RuntimeException("Produit is required");
        }

        Produit produit = produitRepository.findById(inventaire.getProduit().getId())
                .orElseThrow(() -> new RuntimeException("Produit not found with id " + inventaire.getProduit().getId()));

        inventaire.setProduit(produit);
        return inventaireRepository.save(inventaire);
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public Inventaire update(Long id, Inventaire updatedInventaire) {
        Inventaire existingInventaire = inventaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventaire not found with id " + id));

        if (updatedInventaire.getProduit() != null && updatedInventaire.getProduit().getId() != null) {
            Produit produit = produitRepository.findById(updatedInventaire.getProduit().getId())
                    .orElseThrow(() -> new RuntimeException("Produit not found with id " + updatedInventaire.getProduit().getId()));
            existingInventaire.setProduit(produit);
        }

        if (updatedInventaire.getAnneeScolaire() != null) {
            existingInventaire.setAnneeScolaire(updatedInventaire.getAnneeScolaire());
        }
        if (updatedInventaire.getCommentaire() != null) {
            existingInventaire.setCommentaire(updatedInventaire.getCommentaire());
        }
        if (updatedInventaire.getDate() != null) {
            existingInventaire.setDate(updatedInventaire.getDate());
        }
        if (updatedInventaire.getResponsable() != null) {
            existingInventaire.setResponsable(updatedInventaire.getResponsable());
        }
        if (updatedInventaire.getQuantiteRestante() != null) {
            existingInventaire.setQuantiteRestante(updatedInventaire.getQuantiteRestante());
        }

        return inventaireRepository.save(existingInventaire);
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public void deleteById(Long id) {
        Inventaire inventaire = findById(id);
        inventaireRepository.deleteById(id);
    }
}