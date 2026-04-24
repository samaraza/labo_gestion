package labo_gestion_api.service;

import labo_gestion_api.model.Labo;
import labo_gestion_api.model.User;
import labo_gestion_api.repository.LaboRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor  // ✅ أفضل من @Autowired
public class LaboService {

    private final LaboRepository laboRepository;
    private final UserService userService;  // ✅ أضف هذا (إذا أردت ربط المختبرات بالمدارس)

    public List<Labo> getAllLabos() {
        return laboRepository.findAll();
    }

    public Labo getLaboById(Long id) {
        return laboRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Labo not found with id " + id));
    }

    // ✅ جلب المختبرات للمدرسة الحالية (إذا أضفت ecole_id في Labo)
    public List<Labo> getLabosForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return laboRepository.findByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ إضافة مختبر جديد للمدرسة الحالية (إذا أضفت ecole_id في Labo)
    public Labo createLaboForCurrentSchool(Labo labo, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        labo.setEcole(currentUser.getEcole());
        return laboRepository.save(labo);
    }

    public Labo createLabo(Labo labo) {
        return laboRepository.save(labo);
    }

    public Labo updateLabo(Long id, Labo labo) {
        Labo existingLabo = laboRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Labo not found with id " + id));

        if (labo.getLaboType() != null) {
            existingLabo.setLaboType(labo.getLaboType());
        }

        return laboRepository.save(existingLabo);
    }

    public void deleteLabo(Long id) {
        Labo labo = getLaboById(id);
        laboRepository.deleteById(id);
    }
}