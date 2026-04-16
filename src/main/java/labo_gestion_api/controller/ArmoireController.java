package labo_gestion_api.controller;

import labo_gestion_api.model.Armoire;
import labo_gestion_api.model.Produit;
import labo_gestion_api.service.ArmoireService;
import labo_gestion_api.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/armoires")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ArmoireController {

    private final ArmoireService armoireService;
    private final ProduitService produitService;

    // ✅ الحصول على جميع الأرفف
    @GetMapping
    public ResponseEntity<List<Armoire>> getAllArmoires() {
        List<Armoire> armoires = armoireService.findAll();
        return ResponseEntity.ok(armoires);
    }

    // ✅ الحصول على رف بواسطة المعرف (Long)
    @GetMapping("/{id}")
    public ResponseEntity<Armoire> getArmoireById(@PathVariable Long id) {
        Armoire armoire = armoireService.findById(id);
        return ResponseEntity.ok(armoire);
    }

    // ✅ إنشاء رف جديد
    @PostMapping
    public ResponseEntity<Armoire> createArmoire(@RequestBody Armoire armoire) {
        // حفظ الخزانة أولاً (بدون ربط المنتجات)
        Armoire savedArmoire = armoireService.save(armoire);

        // ربط كل منتج موجود بالخزانة المحفوظة
        if (armoire.getProduits() != null) {
            for (Produit produit : armoire.getProduits()) {
                if (produit.getId() != null) {
                    Produit existing = produitService.findById(produit.getId());
                    existing.setArmoire(savedArmoire);   // تعيين المفتاح الأجنبي
                    produitService.save(existing);       // تحديث في قاعدة البيانات
                }
            }
        }

        // إعادة تحميل الخزانة مع المنتجات المرتبطة لعرضها
        savedArmoire = armoireService.findById(savedArmoire.getId());
        return new ResponseEntity<>(savedArmoire, HttpStatus.CREATED);
    }

    // ✅ تحديث رف
    @PutMapping("/{id}")
    public ResponseEntity<Armoire> updateArmoire(
            @PathVariable Long id,
            @RequestBody Armoire updatedArmoire) {

        Armoire armoire = armoireService.findById(id);

        // تحديث الحقول المطلوبة فقط
        if (updatedArmoire.getDesignation() != null) {
            armoire.setDesignation(updatedArmoire.getDesignation());
        }


        if (updatedArmoire.getProduits() != null) {
            // Charger les produits gérés
            List<Produit> managedProduits = new ArrayList<>();
            for (Produit p : updatedArmoire.getProduits()) {
                if (p.getId() != null) {
                    managedProduits.add(produitService.findById(p.getId()));
                }
            }
            armoire.getProduits().clear();
            armoire.getProduits().addAll(managedProduits);
        }
        return ResponseEntity.ok(armoireService.save(armoire));
    }

    // ✅ حذف رف
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArmoire(@PathVariable Long id) {
        armoireService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}