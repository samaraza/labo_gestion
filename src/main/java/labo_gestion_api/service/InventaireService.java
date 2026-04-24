package labo_gestion_api.service;

import labo_gestion_api.model.Inventaire;
import labo_gestion_api.model.Produit;
import labo_gestion_api.model.User;
import labo_gestion_api.repository.InventaireRepository;
import labo_gestion_api.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor  // ✅ أفضل من @Autowired
public class InventaireService {

    private final InventaireRepository inventaireRepository;
    private final ProduitRepository produitRepository;
    private final UserService userService;  // ✅ أضف هذا

    public List<Inventaire> findAll() {
        return inventaireRepository.findAll();
    }

    public Inventaire findById(Long id) {
        return inventaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventaire not found with id " + id));
    }

    // ✅ جلب جميع المخزونات للمدرسة الحالية
    public List<Inventaire> findInventairesForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return inventaireRepository.findByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ جلب المخزونات مع المنتجات للمدرسة الحالية
    public List<Inventaire> findInventairesWithProduitsForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return inventaireRepository.findInventairesWithProduitsByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ إضافة مخزون جديد للمدرسة الحالية
    public Inventaire saveForCurrentSchool(Inventaire inventaire, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }

        if (inventaire.getProduit() == null || inventaire.getProduit().getId() == null) {
            throw new RuntimeException("Produit is required");
        }

        Produit produit = produitRepository.findById(inventaire.getProduit().getId())
                .orElseThrow(() -> new RuntimeException("Produit not found with id " + inventaire.getProduit().getId()));

        inventaire.setProduit(produit);
        inventaire.setEcole(currentUser.getEcole());  // ✅ ربط المخزون بالمدرسة
        return inventaireRepository.save(inventaire);
    }

    public Inventaire save(Inventaire inventaire) {
        if (inventaire.getProduit() == null || inventaire.getProduit().getId() == null) {
            throw new RuntimeException("Produit is required");
        }

        Produit produit = produitRepository.findById(inventaire.getProduit().getId())
                .orElseThrow(() -> new RuntimeException("Produit not found with id " + inventaire.getProduit().getId()));

        inventaire.setProduit(produit);
        return inventaireRepository.save(inventaire);
    }

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

    public void deleteById(Long id) {
        Inventaire inventaire = findById(id);
        inventaireRepository.deleteById(id);
    }
}