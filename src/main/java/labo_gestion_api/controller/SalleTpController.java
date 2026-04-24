package labo_gestion_api.controller;

import labo_gestion_api.model.Armoire;
import labo_gestion_api.model.SalleTp;
import labo_gestion_api.model.User;
import labo_gestion_api.service.ArmoireService;
import labo_gestion_api.service.SalleTpService;
import labo_gestion_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salle-tps")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class SalleTpController {

    private final SalleTpService salleTpService;
    private final ArmoireService armoireService;
    private final UserService userService;  // ✅ أضف هذا

    // ✅ الحصول على جميع قاعات TP (للمدير العام)
    @GetMapping
    public ResponseEntity<List<SalleTp>> getAllSalleTps() {
        List<SalleTp> salleTps = salleTpService.getAllSalleTps();
        return ResponseEntity.ok(salleTps);
    }

    // ✅ جلب قاعات TP للمدرسة الحالية فقط
    @GetMapping("/my-school")
    public ResponseEntity<List<SalleTp>> getSalleTpsForCurrentSchool(Authentication authentication) {
        List<SalleTp> salleTps = salleTpService.getSalleTpsForCurrentSchool(authentication);
        return ResponseEntity.ok(salleTps);
    }

    // ✅ جلب قاعات TP مع الخزائن للمدرسة الحالية
    @GetMapping("/my-school/with-armoires")
    public ResponseEntity<List<SalleTp>> getSalleTpsWithArmoiresForCurrentSchool(Authentication authentication) {
        List<SalleTp> salleTps = salleTpService.getSalleTpsWithArmoiresForCurrentSchool(authentication);
        return ResponseEntity.ok(salleTps);
    }

    // ✅ الحصول على قاعة TP بواسطة المعرف
    @GetMapping("/{id}")
    public ResponseEntity<SalleTp> getSalleTpById(@PathVariable Long id) {
        SalleTp salleTp = salleTpService.getSalleTpById(id);
        return ResponseEntity.ok(salleTp);
    }

    // ✅ إنشاء قاعة TP جديدة للمدرسة الحالية
    @PostMapping("/my-school")
    public ResponseEntity<SalleTp> createSalleTpForCurrentSchool(
            @RequestBody SalleTp salleTp,
            Authentication authentication) {

        // حفظ القاعة مع ربطها بالمدرسة الحالية
        SalleTp saved = salleTpService.createSalleTpForCurrentSchool(salleTp, authentication);

        // ربط الأرفف المختارة بهذه القاعة
        if (salleTp.getArmoires() != null) {
            for (Armoire armoire : salleTp.getArmoires()) {
                if (armoire.getId() != null) {
                    Armoire existing = armoireService.findById(armoire.getId());
                    existing.setSalleTp(saved);
                    armoireService.save(existing);
                }
            }
        }

        // إعادة تحميل القاعة مع الأرفف
        saved = salleTpService.getSalleTpById(saved.getId());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ✅ إنشاء قاعة TP جديدة (قديم)
    @PostMapping
    public ResponseEntity<SalleTp> createSalleTp(@RequestBody SalleTp salleTp) {
        SalleTp saved = salleTpService.createSalleTp(salleTp);

        if (salleTp.getArmoires() != null) {
            for (Armoire armoire : salleTp.getArmoires()) {
                if (armoire.getId() != null) {
                    Armoire existing = armoireService.findById(armoire.getId());
                    existing.setSalleTp(saved);
                    armoireService.save(existing);
                }
            }
        }

        saved = salleTpService.getSalleTpById(saved.getId());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ✅ تحديث قاعة TP
    @PutMapping("/{id}")
    public ResponseEntity<SalleTp> updateSalleTp(@PathVariable Long id, @RequestBody SalleTp salleTp) {
        SalleTp existing = salleTpService.getSalleTpById(id);

        if (salleTp.getNumero() != null) {
            existing.setNumero(salleTp.getNumero());
        }

        if (existing.getArmoires() != null) {
            for (Armoire a : existing.getArmoires()) {
                a.setSalleTp(null);
                armoireService.save(a);
            }
            existing.getArmoires().clear();
        }

        if (salleTp.getArmoires() != null) {
            for (Armoire a : salleTp.getArmoires()) {
                if (a.getId() != null) {
                    Armoire armoire = armoireService.findById(a.getId());
                    armoire.setSalleTp(existing);
                    armoireService.save(armoire);
                    existing.getArmoires().add(armoire);
                }
            }
        }

        return ResponseEntity.ok(salleTpService.updateSalleTp(id, existing));
    }

    // ✅ حذف قاعة TP
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalleTp(@PathVariable Long id) {
        salleTpService.deleteSalleTp(id);
        return ResponseEntity.noContent().build();
    }
}