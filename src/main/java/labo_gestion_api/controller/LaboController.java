package labo_gestion_api.controller;

import labo_gestion_api.model.Labo;
import labo_gestion_api.model.SalleTp;
import labo_gestion_api.repository.SalleTpRepository;
import labo_gestion_api.service.LaboService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/labos")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class LaboController {

    private final LaboService laboService;
    private final SalleTpRepository salleTpRepository;

    @GetMapping
    public ResponseEntity<List<Labo>> getAllLabos() {
        List<Labo> labos = laboService.getAllLabos();
        return ResponseEntity.ok(labos);
    }

    // ✅ تحويل String إلى Long
    @GetMapping("/{id}")
    public ResponseEntity<Labo> getLaboById(@PathVariable String id) {
        Long longId = Long.parseLong(id);
        Labo labo = laboService.getLaboById(longId);
        return ResponseEntity.ok(labo);
    }

    @PostMapping
    public ResponseEntity<Labo> createLabo(@RequestBody Labo labo) {
        // 1. تحميل كائنات SalleTp المدارة من قاعدة البيانات باستخدام الـ IDs المرسلة
        List<SalleTp> managedSalleTps = new ArrayList<>();
        if (labo.getSalleTps() != null) {
            for (SalleTp st : labo.getSalleTps()) {
                if (st.getId() != null) {
                    SalleTp existing = salleTpRepository.findById(st.getId())
                            .orElseThrow(() -> new RuntimeException("SalleTp non trouvée avec id: " + st.getId()));
                    managedSalleTps.add(existing);
                }
            }
        }

        // 2. تعيين القائمة المدارة إلى المختبر
        labo.setSalleTps(managedSalleTps);

        // 3. ربط كل قاعة TP بالمختبر (لضمان الاتجاهين)
        for (SalleTp st : managedSalleTps) {
            st.setLabo(labo);
        }

        // 4. حفظ المختبر (الآن جميع الكائنات مدارة ولن يحدث خطأ)
        Labo savedLabo = laboService.createLabo(labo);
        return new ResponseEntity<>(savedLabo, HttpStatus.CREATED);
    }

    // ✅ تحويل String إلى Long
    @PutMapping("/{id}")
    public ResponseEntity<Labo> updateLabo(@PathVariable Long id, @RequestBody Labo labo) {
        // 1. جلب المختبر الموجود من قاعدة البيانات (مع قاعاته الحالية)
        Labo existingLabo = laboService.getLaboById(id);

        // 2. تحديث الحقول البسيطة
        existingLabo.setLaboType(labo.getLaboType());

        // 3. معالجة قاعات TP: تحميل الكائنات المدارة وتعيينها
        List<SalleTp> newManagedSalleTps = new ArrayList<>();
        if (labo.getSalleTps() != null) {
            for (SalleTp st : labo.getSalleTps()) {
                if (st.getId() != null) {
                    SalleTp existingSt = salleTpRepository.findById(st.getId())
                            .orElseThrow(() -> new RuntimeException("SalleTp non trouvée id: " + st.getId()));
                    newManagedSalleTps.add(existingSt);
                }
            }
        }

        // 4. إزالة العلاقة القديمة بين المختبر وقاعات TP
        existingLabo.getSalleTps().forEach(st -> st.setLabo(null));
        existingLabo.getSalleTps().clear();

        // 5. إضافة العلاقات الجديدة
        for (SalleTp st : newManagedSalleTps) {
            existingLabo.addSalleTp(st);  // هذه الدالة تضبط st.setLabo(existingLabo) وتضيف للقائمة
        }

        // 6. حفظ المختبر المُحدَّث
        Labo updatedLabo = laboService.updateLabo(id, existingLabo);
        return ResponseEntity.ok(updatedLabo);
    }

    // ✅ تحويل String إلى Long
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabo(@PathVariable String id) {
        Long longId = Long.parseLong(id);
        laboService.deleteLabo(longId);
        return ResponseEntity.noContent().build();
    }
}