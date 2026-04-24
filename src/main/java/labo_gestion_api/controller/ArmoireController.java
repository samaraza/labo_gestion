package labo_gestion_api.controller;

import labo_gestion_api.model.Armoire;
import labo_gestion_api.model.Produit;
import labo_gestion_api.model.User;
import labo_gestion_api.service.ArmoireService;
import labo_gestion_api.service.ProduitService;
import labo_gestion_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private final UserService userService;  // ✅ أضف هذا

    // ✅ الحصول على جميع الأرفف (للمدير العام فقط)
    @GetMapping
    public ResponseEntity<List<Armoire>> getAllArmoires() {
        List<Armoire> armoires = armoireService.findAll();
        return ResponseEntity.ok(armoires);
    }

    // ✅ الحصول على أرفف المدرسة الحالية فقط
    @GetMapping("/my-school")
    public ResponseEntity<List<Armoire>> getArmoiresForCurrentSchool(Authentication authentication) {
        List<Armoire> armoires = armoireService.findArmoiresForCurrentSchool(authentication);
        return ResponseEntity.ok(armoires);
    }

    // ✅ الحصول على أرفف المدرسة الحالية مع المنتجات
    @GetMapping("/my-school/with-produits")
    public ResponseEntity<List<Armoire>> getArmoiresWithProduitsForCurrentSchool(Authentication authentication) {
        List<Armoire> armoires = armoireService.findArmoiresWithProduitsForCurrentSchool(authentication);
        return ResponseEntity.ok(armoires);
    }

    // ✅ الحصول على رف بواسطة المعرف (Long)
    @GetMapping("/{id}")
    public ResponseEntity<Armoire> getArmoireById(@PathVariable Long id) {
        Armoire armoire = armoireService.findById(id);
        return ResponseEntity.ok(armoire);
    }

    // ✅ إنشاء رف جديد للمدرسة الحالية
    @PostMapping("/my-school")
    public ResponseEntity<Armoire> createArmoireForCurrentSchool(
            @RequestBody Armoire armoire,
            Authentication authentication) {

        // حفظ الخزانة مع ربطها بالمدرسة الحالية
        Armoire savedArmoire = armoireService.saveForCurrentSchool(armoire, authentication);

        // ربط كل منتج موجود بالخزانة المحفوظة
        if (armoire.getProduits() != null) {
            for (Produit produit : armoire.getProduits()) {
                if (produit.getId() != null) {
                    Produit existing = produitService.findById(produit.getId());
                    existing.setArmoire(savedArmoire);
                    produitService.save(existing);
                }
            }
        }

        // إعادة تحميل الخزانة مع المنتجات المرتبطة
        savedArmoire = armoireService.findById(savedArmoire.getId());
        return new ResponseEntity<>(savedArmoire, HttpStatus.CREATED);
    }

    // ✅ إنشاء رف جديد (قديم)
    @PostMapping
    public ResponseEntity<Armoire> createArmoire(@RequestBody Armoire armoire) {
        Armoire savedArmoire = armoireService.save(armoire);

        if (armoire.getProduits() != null) {
            for (Produit produit : armoire.getProduits()) {
                if (produit.getId() != null) {
                    Produit existing = produitService.findById(produit.getId());
                    existing.setArmoire(savedArmoire);
                    produitService.save(existing);
                }
            }
        }

        savedArmoire = armoireService.findById(savedArmoire.getId());
        return new ResponseEntity<>(savedArmoire, HttpStatus.CREATED);
    }

    // ✅ تحديث رف
    @PutMapping("/{id}")
    public ResponseEntity<Armoire> updateArmoire(
            @PathVariable Long id,
            @RequestBody Armoire updatedArmoire) {

        Armoire armoire = armoireService.findById(id);

        if (updatedArmoire.getDesignation() != null) {
            armoire.setDesignation(updatedArmoire.getDesignation());
        }

        if (updatedArmoire.getProduits() != null) {
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