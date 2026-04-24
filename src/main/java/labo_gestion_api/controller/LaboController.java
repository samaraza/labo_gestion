package labo_gestion_api.controller;

import labo_gestion_api.model.Labo;
import labo_gestion_api.model.SalleTp;
import labo_gestion_api.repository.SalleTpRepository;
import labo_gestion_api.service.LaboService;
import labo_gestion_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Labo>> getAllLabos() {
        List<Labo> labos = laboService.getAllLabos();
        return ResponseEntity.ok(labos);
    }

    // ✅ جلب المختبرات للمدرسة الحالية فقط
    @GetMapping("/my-school")
    public ResponseEntity<List<Labo>> getLabosForCurrentSchool(Authentication authentication) {
        List<Labo> labos = laboService.getLabosForCurrentSchool(authentication);
        return ResponseEntity.ok(labos);
    }

    // ❌ هذا الـ endpoint يسبب الخطأ - قم بإزالته أو أضف الدالة في LaboService
    // @GetMapping("/my-school/with-salles")
    // public ResponseEntity<List<Labo>> getLabosWithSallesForCurrentSchool(Authentication authentication) {
    //     List<Labo> labos = laboService.getLabosWithSallesForCurrentSchool(authentication);
    //     return ResponseEntity.ok(labos);
    // }

    @GetMapping("/{id}")
    public ResponseEntity<Labo> getLaboById(@PathVariable String id) {
        Long longId = Long.parseLong(id);
        Labo labo = laboService.getLaboById(longId);
        return ResponseEntity.ok(labo);
    }

    @PostMapping("/my-school")
    public ResponseEntity<Labo> createLaboForCurrentSchool(
            @RequestBody Labo labo,
            Authentication authentication) {

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

        labo.setSalleTps(managedSalleTps);
        for (SalleTp st : managedSalleTps) {
            st.setLabo(labo);
        }

        Labo savedLabo = laboService.createLaboForCurrentSchool(labo, authentication);
        return new ResponseEntity<>(savedLabo, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<Labo> createLabo(@RequestBody Labo labo) {
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

        labo.setSalleTps(managedSalleTps);
        for (SalleTp st : managedSalleTps) {
            st.setLabo(labo);
        }

        Labo savedLabo = laboService.createLabo(labo);
        return new ResponseEntity<>(savedLabo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Labo> updateLabo(@PathVariable Long id, @RequestBody Labo labo) {
        Labo existingLabo = laboService.getLaboById(id);
        existingLabo.setLaboType(labo.getLaboType());

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

        existingLabo.getSalleTps().forEach(st -> st.setLabo(null));
        existingLabo.getSalleTps().clear();

        for (SalleTp st : newManagedSalleTps) {
            existingLabo.addSalleTp(st);
        }

        Labo updatedLabo = laboService.updateLabo(id, existingLabo);
        return ResponseEntity.ok(updatedLabo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabo(@PathVariable String id) {
        Long longId = Long.parseLong(id);
        laboService.deleteLabo(longId);
        return ResponseEntity.noContent().build();
    }
}