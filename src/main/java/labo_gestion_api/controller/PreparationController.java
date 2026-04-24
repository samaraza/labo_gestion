package labo_gestion_api.controller;

import labo_gestion_api.dto.PreparationRequest;
import labo_gestion_api.model.Preparation;
import labo_gestion_api.model.Produit;
import labo_gestion_api.model.User;
import labo_gestion_api.service.PreparationService;
import labo_gestion_api.service.ProduitService;
import labo_gestion_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/preparations")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class PreparationController {

    private final PreparationService preparationService;
    private final ProduitService produitService;
    private final UserService userService;  // ✅ أضف هذا

    // ✅ الحصول على جميع التحضيرات (للمدير العام)
    @GetMapping
    public ResponseEntity<List<Preparation>> getAllPreparations() {
        List<Preparation> preparations = preparationService.findAll();
        return ResponseEntity.ok(preparations);
    }

    // ✅ جلب التحضيرات للمدرسة الحالية فقط
    @GetMapping("/my-school")
    public ResponseEntity<List<Preparation>> getPreparationsForCurrentSchool(Authentication authentication) {
        List<Preparation> preparations = preparationService.findPreparationsForCurrentSchool(authentication);
        return ResponseEntity.ok(preparations);
    }

    // ✅ جلب التحضيرات مع المنتجات للمدرسة الحالية
    @GetMapping("/my-school/with-produits")
    public ResponseEntity<List<Preparation>> getPreparationsWithProduitsForCurrentSchool(Authentication authentication) {
        List<Preparation> preparations = preparationService.findPreparationsWithProduitsForCurrentSchool(authentication);
        return ResponseEntity.ok(preparations);
    }

    // ✅ الحصول على تحضيرة بواسطة المعرف
    @GetMapping("/{id}")
    public ResponseEntity<Preparation> getPreparationById(@PathVariable Long id) {
        Preparation preparation = preparationService.findById(id);
        return ResponseEntity.ok(preparation);
    }

    // ✅ إنشاء تحضيرة جديدة للمدرسة الحالية
    @PostMapping("/my-school")
    public ResponseEntity<Preparation> createPreparationForCurrentSchool(
            @RequestBody PreparationRequest request,
            Authentication authentication) {

        Preparation preparation = new Preparation();
        preparation.setDesignation(request.getDesignation());
        preparation.setDate(request.getDate() != null ? request.getDate() : LocalDateTime.now());
        preparation.setQuantite(request.getQuantite());

        Produit produit1 = produitService.findById(request.getProduit1Id());
        preparation.setProduit1(produit1);
        preparation.setQuantite1(request.getQuantite1());

        if (request.getProduit2Id() != null) {
            Produit produit2 = produitService.findById(request.getProduit2Id());
            preparation.setProduit2(produit2);
            preparation.setQuantite2(request.getQuantite2());
        }

        Preparation saved = preparationService.saveForCurrentSchool(preparation, authentication);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ✅ إنشاء تحضيرة جديدة (قديم)
    @PostMapping
    public ResponseEntity<Preparation> createPreparation(@RequestBody PreparationRequest request) {
        Preparation preparation = new Preparation();
        preparation.setDesignation(request.getDesignation());
        preparation.setDate(request.getDate() != null ? request.getDate() : LocalDateTime.now());
        preparation.setQuantite(request.getQuantite());

        Produit produit1 = produitService.findById(request.getProduit1Id());
        preparation.setProduit1(produit1);
        preparation.setQuantite1(request.getQuantite1());

        if (request.getProduit2Id() != null) {
            Produit produit2 = produitService.findById(request.getProduit2Id());
            preparation.setProduit2(produit2);
            preparation.setQuantite2(request.getQuantite2());
        }

        Preparation saved = preparationService.save(preparation);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ✅ تحديث تحضيرة
    @PutMapping("/{id}")
    public ResponseEntity<Preparation> updatePreparation(
            @PathVariable Long id,
            @RequestBody Preparation updatedPreparation) {

        Preparation updated = preparationService.update(id, updatedPreparation);
        return ResponseEntity.ok(updated);
    }

    // ✅ حذف تحضيرة
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreparation(@PathVariable Long id) {
        preparationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}