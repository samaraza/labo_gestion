package labo_gestion_api.controller;

import labo_gestion_api.dto.PreparationRequest;
import labo_gestion_api.model.Preparation;
import labo_gestion_api.model.Produit;
import labo_gestion_api.service.PreparationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import labo_gestion_api.service.ProduitService;

@RestController
@RequestMapping("/preparations")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class PreparationController {

    private final PreparationService preparationService;
    private final ProduitService produitService;

    // ✅ الحصول على جميع التحضيرات
    @GetMapping
    public ResponseEntity<List<Preparation>> getAllPreparations() {
        List<Preparation> preparations = preparationService.findAll();
        return ResponseEntity.ok(preparations);
    }

    // ✅ الحصول على تحضيرة بواسطة المعرف
    @GetMapping("/{id}")
    public ResponseEntity<Preparation> getPreparationById(@PathVariable Long id) {
        Preparation preparation = preparationService.findById(id);
        return ResponseEntity.ok(preparation);
    }

    // ✅ إنشاء تحضيرة جديدة
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