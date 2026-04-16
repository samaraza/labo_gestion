package labo_gestion_api.service;


import labo_gestion_api.model.Produit;
import labo_gestion_api.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProduitService {


    @Autowired
    private ProduitRepository produitRepository;

    public List<Produit> findAll() {

        return produitRepository.findAllWithFournisseur();
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public Produit findById(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit not found with id " + id));
    }

    // ✅ تصحيح: إضافة التحقق من عدم تكرار المرجع
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

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public void deleteById(Long id) {
        Produit produit = findById(id);
        produitRepository.deleteById(id);
    }


}
