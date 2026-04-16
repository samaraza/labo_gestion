package labo_gestion_api.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import labo_gestion_api.dto.ProduitRequest;
import labo_gestion_api.model.Fournisseur;
import labo_gestion_api.model.Produit;
import labo_gestion_api.service.FournisseurService;
import labo_gestion_api.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produits")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class ProduitController {

    private final ProduitService produitService;
    private final FournisseurService fournisseurService;  // ✅ أضف هذا

    @GetMapping
    public ResponseEntity<List<Produit>> getAllProduits() {
        return ResponseEntity.ok(produitService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        return ResponseEntity.ok(produitService.findById(id));
    }

    // ✅ تعديل دالة الإضافة
    @PostMapping
    public ResponseEntity<Produit> createProduit(@RequestBody ProduitRequest request) {
        Produit produit = new Produit();
        produit.setDesignation(request.getDesignation());
        produit.setReference(request.getReference());
        produit.setType(request.getType());
        produit.setDateExp(request.getDateExp());
        produit.setCategorie(request.getCategorie());
        produit.setRubrique(request.getRubrique());
        produit.setDurabilite(request.getDurabilite());
        produit.setQuantiteInitiale(request.getQuantiteInitiale());
        produit.setUniteMesure(request.getUniteMesure());

        if (request.getFournisseurId() != null) {
            Fournisseur fournisseur = fournisseurService.findById(request.getFournisseurId());
            produit.setFournisseur(fournisseur);
        }

        Produit saved = produitService.save(produit);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ✅ تعديل دالة التحديث
    @PutMapping("/{id}")
    public ResponseEntity<Produit> updateProduit(@PathVariable Long id,
                                                 @RequestBody ProduitRequest request) {
        Produit produit = produitService.findById(id);

        if (request.getDesignation() != null) produit.setDesignation(request.getDesignation());
        if (request.getReference() != null) produit.setReference(request.getReference());
        if (request.getType() != null) produit.setType(request.getType());
        if (request.getDateExp() != null) produit.setDateExp(request.getDateExp());
        if (request.getCategorie() != null) produit.setCategorie(request.getCategorie());
        if (request.getRubrique() != null) produit.setRubrique(request.getRubrique());
        if (request.getDurabilite() != null) produit.setDurabilite(request.getDurabilite());
        if (request.getQuantiteInitiale() != null) produit.setQuantiteInitiale(request.getQuantiteInitiale());
        if (request.getUniteMesure() != null) produit.setUniteMesure(request.getUniteMesure());

        // ✅ تحديث المورد إذا ورد fournisseurId
        if (request.getFournisseurId() != null) {
            Fournisseur fournisseur = fournisseurService.findById(request.getFournisseurId());
            produit.setFournisseur(fournisseur);
        }

        Produit updated = produitService.save(produit);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        produitService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}