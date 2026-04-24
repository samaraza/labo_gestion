package labo_gestion_api.service;

import labo_gestion_api.model.Preparation;
import labo_gestion_api.model.Produit;
import labo_gestion_api.model.User;
import labo_gestion_api.repository.PreparationRepository;
import labo_gestion_api.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor  // ✅ أفضل من @Autowired
public class PreparationService {

    private final PreparationRepository preparationRepository;
    private final ProduitRepository produitRepository;
    private final UserService userService;  // ✅ أضف هذا

    public List<Preparation> findAll() {
        return preparationRepository.findAllWithProduits();
    }

    public Preparation findById(Long id) {
        return preparationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preparation not found with id " + id));
    }

    // ✅ جلب جميع التحضيرات للمدرسة الحالية
    public List<Preparation> findPreparationsForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return preparationRepository.findByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ جلب التحضيرات مع المنتجات للمدرسة الحالية
    public List<Preparation> findPreparationsWithProduitsForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return preparationRepository.findAllWithProduitsByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ إضافة تحضير جديد للمدرسة الحالية
    @Transactional
    public Preparation saveForCurrentSchool(Preparation preparation, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        if (preparation.getDate() == null) {
            preparation.setDate(LocalDateTime.now());
        }
        preparation.setEcole(currentUser.getEcole());  // ✅ ربط التحضير بالمدرسة
        Preparation saved = preparationRepository.save(preparation);
        return preparationRepository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Preparation not found after save"));
    }

    @Transactional
    public Preparation save(Preparation preparation) {
        if (preparation.getDate() == null) {
            preparation.setDate(LocalDateTime.now());
        }
        Preparation saved = preparationRepository.save(preparation);
        return preparationRepository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Preparation not found after save"));
    }

    private Preparation saveWithOneProduit(Preparation preparation) {
        Produit produit1 = produitRepository.findById(preparation.getProduit1().getId())
                .orElseThrow(() -> new RuntimeException("Produit1 not found with id " + preparation.getProduit1().getId()));

        if (produit1.getQuantiteInitiale() < preparation.getQuantite1()) {
            throw new RuntimeException("Insufficient quantity for produit: " + produit1.getDesignation() +
                    ". Available: " + produit1.getQuantiteInitiale() + ", Required: " + preparation.getQuantite1());
        }

        produit1.setQuantiteInitiale(produit1.getQuantiteInitiale() - preparation.getQuantite1());
        produitRepository.save(produit1);

        return preparationRepository.save(preparation);
    }

    private Preparation saveWithTwoProduits(Preparation preparation) {
        Produit produit1 = produitRepository.findById(preparation.getProduit1().getId())
                .orElseThrow(() -> new RuntimeException("Produit1 not found with id " + preparation.getProduit1().getId()));

        Produit produit2 = produitRepository.findById(preparation.getProduit2().getId())
                .orElseThrow(() -> new RuntimeException("Produit2 not found with id " + preparation.getProduit2().getId()));

        if (produit1.getQuantiteInitiale() < preparation.getQuantite1()) {
            throw new RuntimeException("Insufficient quantity for produit1: " + produit1.getDesignation() +
                    ". Available: " + produit1.getQuantiteInitiale() + ", Required: " + preparation.getQuantite1());
        }

        if (produit2.getQuantiteInitiale() < preparation.getQuantite2()) {
            throw new RuntimeException("Insufficient quantity for produit2: " + produit2.getDesignation() +
                    ". Available: " + produit2.getQuantiteInitiale() + ", Required: " + preparation.getQuantite2());
        }

        produit1.setQuantiteInitiale(produit1.getQuantiteInitiale() - preparation.getQuantite1());
        produit2.setQuantiteInitiale(produit2.getQuantiteInitiale() - preparation.getQuantite2());

        produitRepository.save(produit1);
        produitRepository.save(produit2);

        return preparationRepository.save(preparation);
    }

    @Transactional
    public Preparation update(Long id, Preparation updatedPreparation) {
        Preparation existingPreparation = preparationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preparation not found with id " + id));

        restoreOriginalQuantities(existingPreparation);

        existingPreparation.setDesignation(updatedPreparation.getDesignation());
        existingPreparation.setDate(LocalDateTime.now());
        existingPreparation.setQuantite(updatedPreparation.getQuantite());

        if (updatedPreparation.getProduit1() != null && updatedPreparation.getProduit1().getId() != null) {
            existingPreparation.setProduit1(updatedPreparation.getProduit1());
            existingPreparation.setQuantite1(updatedPreparation.getQuantite1());
        }

        if (updatedPreparation.getProduit2() != null && updatedPreparation.getProduit2().getId() != null) {
            existingPreparation.setProduit2(updatedPreparation.getProduit2());
            existingPreparation.setQuantite2(updatedPreparation.getQuantite2());
        } else {
            existingPreparation.setProduit2(null);
            existingPreparation.setQuantite2(null);
        }

        applyNewQuantities(existingPreparation);

        return preparationRepository.save(existingPreparation);
    }

    private void restoreOriginalQuantities(Preparation preparation) {
        if (preparation.getProduit1() != null && preparation.getProduit1().getId() != null && preparation.getQuantite1() != null) {
            Produit produit1 = produitRepository.findById(preparation.getProduit1().getId())
                    .orElseThrow(() -> new RuntimeException("Produit1 not found with id " + preparation.getProduit1().getId()));
            produit1.setQuantiteInitiale(produit1.getQuantiteInitiale() + preparation.getQuantite1());
            produitRepository.save(produit1);
        }

        if (preparation.getProduit2() != null && preparation.getProduit2().getId() != null && preparation.getQuantite2() != null) {
            Produit produit2 = produitRepository.findById(preparation.getProduit2().getId())
                    .orElseThrow(() -> new RuntimeException("Produit2 not found with id " + preparation.getProduit2().getId()));
            produit2.setQuantiteInitiale(produit2.getQuantiteInitiale() + preparation.getQuantite2());
            produitRepository.save(produit2);
        }
    }

    private void applyNewQuantities(Preparation preparation) {
        if (preparation.getProduit1() != null && preparation.getProduit1().getId() != null && preparation.getQuantite1() != null) {
            Produit produit1 = produitRepository.findById(preparation.getProduit1().getId())
                    .orElseThrow(() -> new RuntimeException("Produit1 not found with id " + preparation.getProduit1().getId()));

            if (produit1.getQuantiteInitiale() < preparation.getQuantite1()) {
                throw new RuntimeException("Insufficient quantity for produit1. Available: " +
                        produit1.getQuantiteInitiale() + ", Required: " + preparation.getQuantite1());
            }

            produit1.setQuantiteInitiale(produit1.getQuantiteInitiale() - preparation.getQuantite1());
            produitRepository.save(produit1);
        }

        if (preparation.getProduit2() != null && preparation.getProduit2().getId() != null && preparation.getQuantite2() != null) {
            Produit produit2 = produitRepository.findById(preparation.getProduit2().getId())
                    .orElseThrow(() -> new RuntimeException("Produit2 not found with id " + preparation.getProduit2().getId()));

            if (produit2.getQuantiteInitiale() < preparation.getQuantite2()) {
                throw new RuntimeException("Insufficient quantity for produit2. Available: " +
                        produit2.getQuantiteInitiale() + ", Required: " + preparation.getQuantite2());
            }

            produit2.setQuantiteInitiale(produit2.getQuantiteInitiale() - preparation.getQuantite2());
            produitRepository.save(produit2);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        Preparation preparation = preparationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preparation not found with id " + id));

        restoreOriginalQuantities(preparation);
        preparationRepository.deleteById(id);
    }
}