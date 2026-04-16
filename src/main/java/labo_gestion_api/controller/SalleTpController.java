package labo_gestion_api.controller;

import labo_gestion_api.model.Armoire;
import labo_gestion_api.model.SalleTp;
import labo_gestion_api.service.ArmoireService;
import labo_gestion_api.service.SalleTpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salle-tps")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class SalleTpController {

    private final SalleTpService salleTpService;
    private final ArmoireService armoireService;

    // ✅ الحصول على جميع قاعات TP
    @GetMapping
    public ResponseEntity<List<SalleTp>> getAllSalleTps() {
        List<SalleTp> salleTps = salleTpService.getAllSalleTps();
        return ResponseEntity.ok(salleTps);
    }

    // ✅ الحصول على قاعة TP بواسطة المعرف
    @GetMapping("/{id}")
    public ResponseEntity<SalleTp> getSalleTpById(@PathVariable Long id) {
        SalleTp salleTp = salleTpService.getSalleTpById(id);
        return ResponseEntity.ok(salleTp);
    }

    // ✅ إنشاء قاعة TP جديدة
    @PostMapping
    public ResponseEntity<SalleTp> createSalleTp(@RequestBody SalleTp salleTp) {
        // 1. حفظ القاعة بدون أرفف (لأن التتالي أزيل)
        SalleTp saved = salleTpService.createSalleTp(salleTp);

        // 2. ربط الأرفف المختارة بهذه القاعة
        if (salleTp.getArmoires() != null) {
            for (Armoire armoire : salleTp.getArmoires()) {
                if (armoire.getId() != null) {
                    Armoire existing = armoireService.findById(armoire.getId());
                    existing.setSalleTp(saved);          // تعيين المفتاح الأجنبي
                    armoireService.save(existing);        // تحديث في قاعدة البيانات
                }
            }
        }

        // 3. إعادة تحميل القاعة مع الأرفف (لضمان ظهورها في الرد)
        saved = salleTpService.getSalleTpById(saved.getId());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }



    // ✅ تحديث قاعة TP
    @PutMapping("/{id}")
    public ResponseEntity<SalleTp> updateSalleTp(@PathVariable Long id, @RequestBody SalleTp salleTp) {
        SalleTp existing = salleTpService.getSalleTpById(id);

        // تحديث رقم القاعة
        if (salleTp.getNumero() != null) {
            existing.setNumero(salleTp.getNumero());
        }

        // إعادة ربط الأرفف: أولاً فك الارتباط عن الأرفف القديمة
        if (existing.getArmoires() != null) {
            for (Armoire a : existing.getArmoires()) {
                a.setSalleTp(null);
                armoireService.save(a);
            }
            existing.getArmoires().clear();
        }

        // ثم ربط الأرفف الجديدة
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