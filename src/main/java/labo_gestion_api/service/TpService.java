package labo_gestion_api.service;

import labo_gestion_api.model.*;
import labo_gestion_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor  // ✅ أفضل من @Autowired
public class TpService {

    private final TpRepository tpRepository;
    private final UserRepository userRepository;
    private final SalleTpRepository salleTpRepository;
    private final PreparationRepository preparationRepository;
    private final ProduitRepository produitRepository;
    private final UserService userService;  // ✅ أضف هذا

    public List<Tp> findAll() {
        List<Tp> tps = tpRepository.findAll();
        for (Tp tp : tps) {
            Hibernate.initialize(tp.getPreparations());
            Hibernate.initialize(tp.getProduits());
            if (tp.getPreparations() != null) {
                for (PreparationTP prepTP : tp.getPreparations()) {
                    Hibernate.initialize(prepTP.getPreparation());
                }
            }
            if (tp.getProduits() != null) {
                for (ProduitTP prodTP : tp.getProduits()) {
                    Hibernate.initialize(prodTP.getProduit());
                }
            }
        }
        return tps;
    }

    public Tp findById(Long id) {
        return tpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tp not found with id " + id));
    }

    // ✅ جلب جميع TPs للمدرسة الحالية
    public List<Tp> findTpsForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return tpRepository.findByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ جلب TPs مع التفاصيل للمدرسة الحالية
    public List<Tp> findTpsWithDetailsForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return tpRepository.findAllWithDetailsByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ إضافة TP جديد للمدرسة الحالية
    @Transactional
    public Tp saveForCurrentSchool(Tp tp, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }

        tp.setEcole(currentUser.getEcole());  // ✅ ربط TP بالمدرسة

        // التحقق من وجود الأستاذ
        if (tp.getProf() == null || tp.getProf().getId() == null) {
            throw new RuntimeException("Professor is required");
        }
        User prof = userRepository.findById(tp.getProf().getId())
                .orElseThrow(() -> new RuntimeException("User not found with id " + tp.getProf().getId()));
        tp.setProf(prof);

        // التحقق من وجود الساعة
        if (tp.getSalleTp() == null || tp.getSalleTp().getId() == null) {
            throw new RuntimeException("Salle TP is required");
        }
        SalleTp salleTp = salleTpRepository.findById(tp.getSalleTp().getId())
                .orElseThrow(() -> new RuntimeException("SalleTp not found with id " + tp.getSalleTp().getId()));
        tp.setSalleTp(salleTp);

        // التحقق من الكميات والمنتجات في التحضيرات
        if (tp.getPreparations() != null) {
            for (PreparationTP preparationTP : tp.getPreparations()) {
                if (preparationTP.getPreparation() == null || preparationTP.getPreparation().getId() == null) {
                    throw new RuntimeException("Preparation is required");
                }
                Preparation preparation = preparationRepository.findById(preparationTP.getPreparation().getId())
                        .orElseThrow(() -> new RuntimeException("Preparation not found with id " + preparationTP.getPreparation().getId()));

                if (preparation.getQuantite() < preparationTP.getQuantite()) {
                    throw new RuntimeException("Insufficient quantity for preparation: " + preparation.getDesignation() +
                            ". Available: " + preparation.getQuantite() + ", Required: " + preparationTP.getQuantite());
                }

                preparation.setQuantite(preparation.getQuantite() - preparationTP.getQuantite());
                preparationRepository.save(preparation);
            }
        }

        // التحقق من الكميات والمنتجات في منتجات TP
        if (tp.getProduits() != null) {
            for (ProduitTP produitTP : tp.getProduits()) {
                if (produitTP.getProduit() == null || produitTP.getProduit().getId() == null) {
                    throw new RuntimeException("Product is required");
                }
                Produit produit = produitRepository.findById(produitTP.getProduit().getId())
                        .orElseThrow(() -> new RuntimeException("Produit not found with id " + produitTP.getProduit().getId()));

                if (produit.getQuantiteInitiale() < produitTP.getQuantite()) {
                    throw new RuntimeException("Insufficient quantity for product: " + produit.getDesignation() +
                            ". Available: " + produit.getQuantiteInitiale() + ", Required: " + produitTP.getQuantite());
                }

                produit.setQuantiteInitiale(produit.getQuantiteInitiale() - produitTP.getQuantite());
                produitRepository.save(produit);
            }
        }

        return tpRepository.save(tp);
    }

    @Transactional
    public Tp save(Tp tp) {
        // التحقق من وجود الأستاذ
        if (tp.getProf() == null || tp.getProf().getId() == null) {
            throw new RuntimeException("Professor is required");
        }
        User prof = userRepository.findById(tp.getProf().getId())
                .orElseThrow(() -> new RuntimeException("User not found with id " + tp.getProf().getId()));
        tp.setProf(prof);

        // التحقق من وجود الساعة
        if (tp.getSalleTp() == null || tp.getSalleTp().getId() == null) {
            throw new RuntimeException("Salle TP is required");
        }
        SalleTp salleTp = salleTpRepository.findById(tp.getSalleTp().getId())
                .orElseThrow(() -> new RuntimeException("SalleTp not found with id " + tp.getSalleTp().getId()));
        tp.setSalleTp(salleTp);

        // التحقق من الكميات والمنتجات في التحضيرات
        if (tp.getPreparations() != null) {
            for (PreparationTP preparationTP : tp.getPreparations()) {
                if (preparationTP.getPreparation() == null || preparationTP.getPreparation().getId() == null) {
                    throw new RuntimeException("Preparation is required");
                }
                Preparation preparation = preparationRepository.findById(preparationTP.getPreparation().getId())
                        .orElseThrow(() -> new RuntimeException("Preparation not found with id " + preparationTP.getPreparation().getId()));

                if (preparation.getQuantite() < preparationTP.getQuantite()) {
                    throw new RuntimeException("Insufficient quantity for preparation: " + preparation.getDesignation() +
                            ". Available: " + preparation.getQuantite() + ", Required: " + preparationTP.getQuantite());
                }

                preparation.setQuantite(preparation.getQuantite() - preparationTP.getQuantite());
                preparationRepository.save(preparation);
            }
        }

        // التحقق من الكميات والمنتجات في منتجات TP
        if (tp.getProduits() != null) {
            for (ProduitTP produitTP : tp.getProduits()) {
                if (produitTP.getProduit() == null || produitTP.getProduit().getId() == null) {
                    throw new RuntimeException("Product is required");
                }
                Produit produit = produitRepository.findById(produitTP.getProduit().getId())
                        .orElseThrow(() -> new RuntimeException("Produit not found with id " + produitTP.getProduit().getId()));

                if (produit.getQuantiteInitiale() < produitTP.getQuantite()) {
                    throw new RuntimeException("Insufficient quantity for product: " + produit.getDesignation() +
                            ". Available: " + produit.getQuantiteInitiale() + ", Required: " + produitTP.getQuantite());
                }

                produit.setQuantiteInitiale(produit.getQuantiteInitiale() - produitTP.getQuantite());
                produitRepository.save(produit);
            }
        }

        return tpRepository.save(tp);
    }

    @Transactional
    public Tp update(Long id, Tp updatedTp) {
        Tp existingTp = tpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tp not found with id " + id));

        restoreOriginalQuantities(existingTp);

        if (updatedTp.getType() != null) {
            existingTp.setType(updatedTp.getType());
        }
        if (updatedTp.getJourTp() != null) {
            existingTp.setJourTp(updatedTp.getJourTp());
        }
        if (updatedTp.getNiveauScolaire() != null) {
            existingTp.setNiveauScolaire(updatedTp.getNiveauScolaire());
        }

        if (updatedTp.getProf() != null && updatedTp.getProf().getId() != null) {
            User prof = userRepository.findById(updatedTp.getProf().getId())
                    .orElseThrow(() -> new RuntimeException("User not found with id " + updatedTp.getProf().getId()));
            existingTp.setProf(prof);
        }

        if (updatedTp.getSalleTp() != null && updatedTp.getSalleTp().getId() != null) {
            SalleTp salleTp = salleTpRepository.findById(updatedTp.getSalleTp().getId())
                    .orElseThrow(() -> new RuntimeException("SalleTp not found with id " + updatedTp.getSalleTp().getId()));
            existingTp.setSalleTp(salleTp);
        }

        if (updatedTp.getPreparations() != null) {
            existingTp.getPreparations().clear();
            for (PreparationTP preparationTP : updatedTp.getPreparations()) {
                existingTp.addPreparation(preparationTP);
            }
        }

        if (updatedTp.getProduits() != null) {
            existingTp.getProduits().clear();
            for (ProduitTP produitTP : updatedTp.getProduits()) {
                existingTp.addProduit(produitTP);
            }
        }

        applyNewQuantities(existingTp);

        return tpRepository.save(existingTp);
    }

    @Transactional
    public void deleteById(Long id) {
        Tp existingTp = tpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tp not found with id " + id));

        restoreOriginalQuantities(existingTp);
        tpRepository.deleteById(id);
    }

    private void restoreOriginalQuantities(Tp tp) {
        if (tp.getPreparations() != null) {
            for (PreparationTP preparationTP : tp.getPreparations()) {
                if (preparationTP.getPreparation() != null && preparationTP.getPreparation().getId() != null) {
                    Preparation preparation = preparationRepository.findById(preparationTP.getPreparation().getId())
                            .orElse(null);
                    if (preparation != null) {
                        preparation.setQuantite(preparation.getQuantite() + preparationTP.getQuantite());
                        preparationRepository.save(preparation);
                    }
                }
            }
        }

        if (tp.getProduits() != null) {
            for (ProduitTP produitTP : tp.getProduits()) {
                if (produitTP.getProduit() != null && produitTP.getProduit().getId() != null) {
                    Produit produit = produitRepository.findById(produitTP.getProduit().getId())
                            .orElse(null);
                    if (produit != null) {
                        produit.setQuantiteInitiale(produit.getQuantiteInitiale() + produitTP.getQuantite());
                        produitRepository.save(produit);
                    }
                }
            }
        }
    }

    private void applyNewQuantities(Tp tp) {
        if (tp.getPreparations() != null) {
            for (PreparationTP preparationTP : tp.getPreparations()) {
                if (preparationTP.getPreparation() != null && preparationTP.getPreparation().getId() != null) {
                    Preparation preparation = preparationRepository.findById(preparationTP.getPreparation().getId())
                            .orElseThrow(() -> new RuntimeException("Preparation not found"));

                    if (preparation.getQuantite() < preparationTP.getQuantite()) {
                        throw new RuntimeException("Insufficient quantity for preparation");
                    }

                    preparation.setQuantite(preparation.getQuantite() - preparationTP.getQuantite());
                    preparationRepository.save(preparation);
                }
            }
        }

        if (tp.getProduits() != null) {
            for (ProduitTP produitTP : tp.getProduits()) {
                if (produitTP.getProduit() != null && produitTP.getProduit().getId() != null) {
                    Produit produit = produitRepository.findById(produitTP.getProduit().getId())
                            .orElseThrow(() -> new RuntimeException("Produit not found"));

                    if (produit.getQuantiteInitiale() < produitTP.getQuantite()) {
                        throw new RuntimeException("Insufficient quantity for product");
                    }

                    produit.setQuantiteInitiale(produit.getQuantiteInitiale() - produitTP.getQuantite());
                    produitRepository.save(produit);
                }
            }
        }
    }

    public List<Tp> findAllWithDetails() {
        return tpRepository.findAllWithDetails();
    }
}