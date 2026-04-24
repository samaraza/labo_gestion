package labo_gestion_api.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import labo_gestion_api.dto.InventaireRequest;
import labo_gestion_api.model.Inventaire;
import labo_gestion_api.model.Produit;
import labo_gestion_api.repository.ProduitRepository;
import labo_gestion_api.service.InventaireService;
import labo_gestion_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventaires")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class InventaireController {

    private final InventaireService inventaireService;
    private final ProduitRepository produitRepository;
    private final UserService userService;  // ✅ أضف هذا

    // ✅ الحصول على جميع المخزونات (للمدير العام)
    @GetMapping
    public ResponseEntity<List<Inventaire>> getAllInventaires() {
        List<Inventaire> inventaires = inventaireService.findAll();
        return ResponseEntity.ok(inventaires);
    }

    // ✅ جلب المخزونات للمدرسة الحالية فقط
    @GetMapping("/my-school")
    public ResponseEntity<List<Inventaire>> getInventairesForCurrentSchool(Authentication authentication) {
        List<Inventaire> inventaires = inventaireService.findInventairesForCurrentSchool(authentication);
        return ResponseEntity.ok(inventaires);
    }

    // ✅ جلب المخزونات مع المنتجات للمدرسة الحالية
    @GetMapping("/my-school/with-produits")
    public ResponseEntity<List<Inventaire>> getInventairesWithProduitsForCurrentSchool(Authentication authentication) {
        List<Inventaire> inventaires = inventaireService.findInventairesWithProduitsForCurrentSchool(authentication);
        return ResponseEntity.ok(inventaires);
    }

    // ✅ الحصول على مخزون بواسطة المعرف
    @GetMapping("/{id}")
    public ResponseEntity<Inventaire> getInventaireById(@PathVariable Long id) {
        Inventaire inventaire = inventaireService.findById(id);
        return ResponseEntity.ok(inventaire);
    }

    // ✅ إنشاء مخزون جديد للمدرسة الحالية
    @PostMapping("/my-school")
    public ResponseEntity<Inventaire> createInventaireForCurrentSchool(
            @RequestBody InventaireRequest request,
            Authentication authentication) {

        Inventaire inventaire = new Inventaire();
        inventaire.setAnneeScolaire(request.getAnneeScolaire());
        inventaire.setCommentaire(request.getCommentaire());
        inventaire.setDate(request.getDate());
        inventaire.setResponsable(request.getResponsable());
        inventaire.setQuantiteRestante(request.getQuantiteRestante());

        Produit produit = produitRepository.findById(request.getProduitId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec id: " + request.getProduitId()));
        inventaire.setProduit(produit);

        Inventaire savedInventaire = inventaireService.saveForCurrentSchool(inventaire, authentication);
        return new ResponseEntity<>(savedInventaire, HttpStatus.CREATED);
    }

    // ✅ إنشاء مخزون جديد (قديم)
    @PostMapping
    public ResponseEntity<Inventaire> createInventaire(@RequestBody InventaireRequest request) {
        Inventaire inventaire = new Inventaire();
        inventaire.setAnneeScolaire(request.getAnneeScolaire());
        inventaire.setCommentaire(request.getCommentaire());
        inventaire.setDate(request.getDate());
        inventaire.setResponsable(request.getResponsable());
        inventaire.setQuantiteRestante(request.getQuantiteRestante());

        Produit produit = produitRepository.findById(request.getProduitId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec id: " + request.getProduitId()));
        inventaire.setProduit(produit);

        return new ResponseEntity<>(inventaireService.save(inventaire), HttpStatus.CREATED);
    }

    // ✅ تحديث مخزون
    @PutMapping("/{id}")
    public ResponseEntity<Inventaire> updateInventaire(
            @PathVariable Long id,
            @RequestBody InventaireRequest request) {

        Inventaire existingInventaire = inventaireService.findById(id);

        existingInventaire.setAnneeScolaire(request.getAnneeScolaire());
        existingInventaire.setCommentaire(request.getCommentaire());
        existingInventaire.setDate(request.getDate());
        existingInventaire.setResponsable(request.getResponsable());
        existingInventaire.setQuantiteRestante(request.getQuantiteRestante());

        Produit produit = produitRepository.findById(request.getProduitId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec id: " + request.getProduitId()));
        existingInventaire.setProduit(produit);

        Inventaire updated = inventaireService.update(id, existingInventaire);
        return ResponseEntity.ok(updated);
    }

    // ✅ حذف مخزون
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventaire(@PathVariable Long id) {
        inventaireService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}