package labo_gestion_api.service;


import labo_gestion_api.model.Preparation;
import labo_gestion_api.model.Produit;
import labo_gestion_api.repository.PreparationRepository;
import labo_gestion_api.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PreparationService {

    @Autowired
    private PreparationRepository preparationRepository;

    @Autowired
    private ProduitRepository produitRepository;

    public List<Preparation> findAll() {

        return preparationRepository.findAllWithProduits();
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public Preparation findById(Long id) {
        return preparationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preparation not found with id " + id));
    }

    // ✅ تصحيح: حفظ التحضير وتحديث الكميات
    @Transactional
    public Preparation save(Preparation preparation) {
        if (preparation.getDate() == null) {
            preparation.setDate(LocalDateTime.now());
        }
        Preparation saved = preparationRepository.save(preparation);
        // إعادة جلب التحضيرة مع تحميل produit1 و produit2
        return preparationRepository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Preparation not found after save"));
    }

    private Preparation saveWithOneProduit(Preparation preparation) {
        Produit produit1 = produitRepository.findById(preparation.getProduit1().getId())
                .orElseThrow(() -> new RuntimeException("Produit1 not found with id " + preparation.getProduit1().getId()));

        // التحقق من الكمية المتوفرة
        if (produit1.getQuantiteInitiale() < preparation.getQuantite1()) {
            throw new RuntimeException("Insufficient quantity for produit: " + produit1.getDesignation() +
                    ". Available: " + produit1.getQuantiteInitiale() + ", Required: " + preparation.getQuantite1());
        }

        // تحديث الكمية
        produit1.setQuantiteInitiale(produit1.getQuantiteInitiale() - preparation.getQuantite1());
        produitRepository.save(produit1);

        return preparationRepository.save(preparation);
    }

    private Preparation saveWithTwoProduits(Preparation preparation) {
        Produit produit1 = produitRepository.findById(preparation.getProduit1().getId())
                .orElseThrow(() -> new RuntimeException("Produit1 not found with id " + preparation.getProduit1().getId()));

        Produit produit2 = produitRepository.findById(preparation.getProduit2().getId())
                .orElseThrow(() -> new RuntimeException("Produit2 not found with id " + preparation.getProduit2().getId()));

        // التحقق من الكميات المتوفرة
        if (produit1.getQuantiteInitiale() < preparation.getQuantite1()) {
            throw new RuntimeException("Insufficient quantity for produit1: " + produit1.getDesignation() +
                    ". Available: " + produit1.getQuantiteInitiale() + ", Required: " + preparation.getQuantite1());
        }

        if (produit2.getQuantiteInitiale() < preparation.getQuantite2()) {
            throw new RuntimeException("Insufficient quantity for produit2: " + produit2.getDesignation() +
                    ". Available: " + produit2.getQuantiteInitiale() + ", Required: " + preparation.getQuantite2());
        }

        // تحديث الكميات
        produit1.setQuantiteInitiale(produit1.getQuantiteInitiale() - preparation.getQuantite1());
        produit2.setQuantiteInitiale(produit2.getQuantiteInitiale() - preparation.getQuantite2());

        produitRepository.save(produit1);
        produitRepository.save(produit2);

        return preparationRepository.save(preparation);
    }

    // ✅ تصحيح: تحديث التحضير
    @Transactional
    public Preparation update(Long id, Preparation updatedPreparation) {
        Preparation existingPreparation = preparationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preparation not found with id " + id));

        // استعادة الكميات الأصلية
        restoreOriginalQuantities(existingPreparation);

        // تحديث البيانات
        existingPreparation.setDesignation(updatedPreparation.getDesignation());
        existingPreparation.setDate(LocalDateTime.now());
        existingPreparation.setQuantite(updatedPreparation.getQuantite());

        // تحديث المنتج الأول
        if (updatedPreparation.getProduit1() != null && updatedPreparation.getProduit1().getId() != null) {
            existingPreparation.setProduit1(updatedPreparation.getProduit1());
            existingPreparation.setQuantite1(updatedPreparation.getQuantite1());
        }

        // تحديث المنتج الثاني (إذا كان موجوداً)
        if (updatedPreparation.getProduit2() != null && updatedPreparation.getProduit2().getId() != null) {
            existingPreparation.setProduit2(updatedPreparation.getProduit2());
            existingPreparation.setQuantite2(updatedPreparation.getQuantite2());
        } else {
            existingPreparation.setProduit2(null);
            existingPreparation.setQuantite2(null);
        }

        // تطبيق الكميات الجديدة
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

    // ✅ تصحيح: حذف التحضير واستعادة الكميات
    @Transactional
    public void deleteById(Long id) {
        Preparation preparation = preparationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preparation not found with id " + id));

        // استعادة الكميات الأصلية
        restoreOriginalQuantities(preparation);

        preparationRepository.deleteById(id);
    }
}

