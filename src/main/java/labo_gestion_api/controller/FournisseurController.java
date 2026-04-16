package labo_gestion_api.controller;

import labo_gestion_api.model.Fournisseur;
import labo_gestion_api.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fournisseurs")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    // ✅ الحصول على جميع الموردين
    @GetMapping
    public ResponseEntity<List<Fournisseur>> getAllFournisseurs() {
        List<Fournisseur> fournisseurs = fournisseurService.findAll();
        return ResponseEntity.ok(fournisseurs);
    }

    // ✅ الحصول على مورد بواسطة المعرف
    @GetMapping("/{id}")
    public ResponseEntity<Fournisseur> getFournisseurById(@PathVariable Long id) {
        Fournisseur fournisseur = fournisseurService.findById(id);
        return ResponseEntity.ok(fournisseur);
    }

    // ✅ إنشاء مورد جديد
    @PostMapping
    public ResponseEntity<Fournisseur> createFournisseur(@RequestBody Fournisseur fournisseur) {
        Fournisseur savedFournisseur = fournisseurService.save(fournisseur);
        return new ResponseEntity<>(savedFournisseur, HttpStatus.CREATED);
    }

    // ✅ تحديث مورد
    @PutMapping("/{id}")
    public ResponseEntity<Fournisseur> updateFournisseur(
            @PathVariable Long id,
            @RequestBody Fournisseur updatedFournisseur) {

        Fournisseur fournisseur = fournisseurService.findById(id);

        // تحديث الحقول المطلوبة فقط
        if (updatedFournisseur.getNom() != null) {
            fournisseur.setNom(updatedFournisseur.getNom());
        }
        if (updatedFournisseur.getAdresse() != null) {
            fournisseur.setAdresse(updatedFournisseur.getAdresse());
        }
        if (updatedFournisseur.getEmail() != null) {
            fournisseur.setEmail(updatedFournisseur.getEmail());
        }
        if (updatedFournisseur.getNmrTel() != null) {
            fournisseur.setNmrTel(updatedFournisseur.getNmrTel());
        }

        Fournisseur updated = fournisseurService.save(fournisseur);
        return ResponseEntity.ok(updated);
    }

    // ✅ حذف مورد
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFournisseur(@PathVariable Long id) {
        fournisseurService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}