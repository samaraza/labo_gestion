package labo_gestion_api.service;

import io.jsonwebtoken.io.IOException;
import labo_gestion_api.model.Commande;
import labo_gestion_api.model.Produit;
import labo_gestion_api.model.ProduitCommande;
import labo_gestion_api.model.User;  // ✅ أضف هذا import
import labo_gestion_api.repository.CommandeRepository;
import labo_gestion_api.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;  // ✅ أضف هذا import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;  // ✅ أضف final
    private final ProduitRepository produitRepository;    // ✅ أضف final
    private final UserService userService;                 // ✅ هذا صحيح

    public List<Commande> findAll() {
        return commandeRepository.findAllWithUsersAndFournisseurs();
    }

    public Commande findById(String id) {
        Long longId = Long.parseLong(id);
        return commandeRepository.findById(longId)
                .orElseThrow(() -> new RuntimeException("Commande not found with id " + id));
    }

    // ✅ جلب جميع الطلبيات للمدرسة الحالية
    public List<Commande> findCommandesForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return commandeRepository.findByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ جلب الطلبيات مع التفاصيل للمدرسة الحالية
    public List<Commande> findCommandesWithDetailsForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return commandeRepository.findAllWithUsersAndFournisseursByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ إضافة طلبية جديدة للمدرسة الحالية
    @Transactional
    public Commande saveForCurrentSchool(Commande commande, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        commande.setEcole(currentUser.getEcole());
        return save(commande);
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

    public void deleteById(String id) {
        Long longId = Long.parseLong(id);
        Commande commande = findById(id);
        commandeRepository.deleteById(longId);
    }

    public ByteArrayInputStream generateCommandePdf(String id) throws IOException, java.io.IOException {
        Commande commande = findById(id);
        return PdfService.generateCommandePdf(commande);
    }
}