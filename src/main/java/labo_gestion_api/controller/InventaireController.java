package labo_gestion_api.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import labo_gestion_api.dto.InventaireRequest;
import labo_gestion_api.model.Inventaire;
import labo_gestion_api.model.Produit;
import labo_gestion_api.repository.ProduitRepository;
import labo_gestion_api.service.InventaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventaires")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor

public class InventaireController {

    private final InventaireService inventaireService;
    private final ProduitRepository produitRepository;


    // ✅ الحصول على جميع المخزونات
    @GetMapping
    public ResponseEntity<List<Inventaire>> getAllInventaires() {
        List<Inventaire> inventaires = inventaireService.findAll();
        return ResponseEntity.ok(inventaires);
    }

    // ✅ الحصول على مخزون بواسطة المعرف
    @GetMapping("/{id}")
    public ResponseEntity<Inventaire> getInventaireById(@PathVariable Long id) {
        Inventaire inventaire = inventaireService.findById(id);
        return ResponseEntity.ok(inventaire);
    }

    // ✅ إنشاء مخزون جديد

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

        // تحديث الحقول
        existingInventaire.setAnneeScolaire(request.getAnneeScolaire());
        existingInventaire.setCommentaire(request.getCommentaire());
        existingInventaire.setDate(request.getDate()); // تحويل التاريخ
        existingInventaire.setResponsable(request.getResponsable());
        existingInventaire.setQuantiteRestante(request.getQuantiteRestante());

        // تحديث المنتج (produit)
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