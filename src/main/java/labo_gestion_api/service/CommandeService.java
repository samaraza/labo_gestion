package labo_gestion_api.service;

import io.jsonwebtoken.io.IOException;
import labo_gestion_api.model.Commande;
import labo_gestion_api.model.Produit;
import labo_gestion_api.model.ProduitCommande;
import labo_gestion_api.repository.CommandeRepository;
import labo_gestion_api.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class CommandeService {

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private ProduitRepository produitRepository;

    public List<Commande> findAll() {
        return commandeRepository.findAllWithUsersAndFournisseurs();
    }

    // ✅ تحويل String إلى Long
    public Commande findById(String id) {
        Long longId = Long.parseLong(id);
        return commandeRepository.findById(longId)
                .orElseThrow(() -> new RuntimeException("Commande not found with id " + id));
    }

    @Transactional
    public Commande save(Commande commande) {
        if (commande.getProduits() != null) {
            for (ProduitCommande pc : commande.getProduits()) {
                if (pc.getProduit() != null && pc.getProduit().getId() != null) {
                    Produit produit = produitRepository.findById(pc.getProduit().getId())
                            .orElseThrow(() -> new RuntimeException("Produit not found with id " + pc.getProduit().getId()));

                    produit.setQuantiteInitiale(produit.getQuantiteInitiale() + pc.getQuantiteAjoutee());
                    produitRepository.save(produit);
                }
            }
        }
        return commandeRepository.save(commande);
    }

    // ✅ تحويل String إلى Long
    public void deleteById(String id) {
        Long longId = Long.parseLong(id);
        Commande commande = findById(id);
        commandeRepository.deleteById(longId);
    }

    // ✅ تحويل String إلى Long
    public ByteArrayInputStream generateCommandePdf(String id) throws IOException, java.io.IOException {
        Commande commande = findById(id);
        return PdfService.generateCommandePdf(commande);
    }
}
