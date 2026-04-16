package labo_gestion_api.service;


import labo_gestion_api.model.*;
import labo_gestion_api.repository.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TpService {

    @Autowired
    private TpRepository tpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalleTpRepository salleTpRepository;

    @Autowired
    private PreparationRepository preparationRepository;

    @Autowired
    private ProduitRepository produitRepository;

    public List<Tp> findAll() {
        List<Tp> tps = tpRepository.findAll();
        for (Tp tp : tps) {
            Hibernate.initialize(tp.getPreparations());
            Hibernate.initialize(tp.getProduits());
            // لتحميل كائنات Preparation و Produit داخل كل PreparationTP
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

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public Tp findById(Long id) {
        return tpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tp not found with id " + id));
    }

    // ✅ تصحيح: حفظ TP مع التحقق من الكميات
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

    // ✅ تصحيح: تحديث TP
    @Transactional
    public Tp update(Long id, Tp updatedTp) {
        Tp existingTp = tpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tp not found with id " + id));

        // استعادة الكميات الأصلية قبل التحديث
        restoreOriginalQuantities(existingTp);

        // تحديث الحقول الأساسية
        if (updatedTp.getType() != null) {
            existingTp.setType(updatedTp.getType());
        }
        if (updatedTp.getJourTp() != null) {
            existingTp.setJourTp(updatedTp.getJourTp());
        }
        if (updatedTp.getNiveauScolaire() != null) {
            existingTp.setNiveauScolaire(updatedTp.getNiveauScolaire());
        }

        // تحديث الأستاذ
        if (updatedTp.getProf() != null && updatedTp.getProf().getId() != null) {
            User prof = userRepository.findById(updatedTp.getProf().getId())
                    .orElseThrow(() -> new RuntimeException("User not found with id " + updatedTp.getProf().getId()));
            existingTp.setProf(prof);
        }

        // تحديث الساعة
        if (updatedTp.getSalleTp() != null && updatedTp.getSalleTp().getId() != null) {
            SalleTp salleTp = salleTpRepository.findById(updatedTp.getSalleTp().getId())
                    .orElseThrow(() -> new RuntimeException("SalleTp not found with id " + updatedTp.getSalleTp().getId()));
            existingTp.setSalleTp(salleTp);
        }

        // تحديث التحضيرات
        if (updatedTp.getPreparations() != null) {
            existingTp.getPreparations().clear();
            for (PreparationTP preparationTP : updatedTp.getPreparations()) {
                existingTp.addPreparation(preparationTP);
            }
        }

        // تحديث المنتجات
        if (updatedTp.getProduits() != null) {
            existingTp.getProduits().clear();
            for (ProduitTP produitTP : updatedTp.getProduits()) {
                existingTp.addProduit(produitTP);
            }
        }

        // تطبيق الكميات الجديدة
        applyNewQuantities(existingTp);

        return tpRepository.save(existingTp);
    }

    // ✅ تصحيح: حذف TP
    @Transactional
    public void deleteById(Long id) {
        Tp existingTp = tpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tp not found with id " + id));

        // استعادة الكميات الأصلية قبل الحذف
        restoreOriginalQuantities(existingTp);

        tpRepository.deleteById(id);
    }

    // ✅ استعادة الكميات الأصلية
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

    // ✅ تطبيق الكميات الجديدة
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
