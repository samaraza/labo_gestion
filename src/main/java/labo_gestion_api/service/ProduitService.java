package labo_gestion_api.service;

import labo_gestion_api.model.Produit;
import labo_gestion_api.model.User;
import labo_gestion_api.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor  // ✅ أفضل من @Autowired
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final UserService userService;  // ✅ أضف هذا

    public List<Produit> findAll() {
        return produitRepository.findAllWithFournisseur();
    }

    public Produit findById(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit not found with id " + id));
    }

    // ✅ جلب جميع المنتجات للمدرسة الحالية
    public List<Produit> findProduitsForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return produitRepository.findByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ جلب المنتجات مع المورد للمدرسة الحالية
    public List<Produit> findProduitsWithFournisseurForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return produitRepository.findAllWithFournisseurByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ إضافة منتج جديد للمدرسة الحالية
    public Produit saveForCurrentSchool(Produit produit, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }

        // التحقق من عدم وجود مرجع مكرر
        if (produit.getReference() != null && !produit.getReference().isEmpty()) {
            boolean exists = produitRepository.existsByReference(produit.getReference());
            if (exists && produit.getId() == null) {
                throw new RuntimeException("Produit with reference " + produit.getReference() + " already exists");
            }
        }

        produit.setEcole(currentUser.getEcole());  // ✅ ربط المنتج بالمدرسة
        return produitRepository.save(produit);
    }

    public Produit save(Produit produit) {
        // التحقق من عدم وجود مرجع مكرر
        if (produit.getReference() != null && !produit.getReference().isEmpty()) {
            boolean exists = produitRepository.existsByReference(produit.getReference());
            if (exists && produit.getId() == null) {
                throw new RuntimeException("Produit with reference " + produit.getReference() + " already exists");
            }
        }
        return produitRepository.save(produit);
    }

    public void deleteById(Long id) {
        Produit produit = findById(id);
        produitRepository.deleteById(id);
    }
}