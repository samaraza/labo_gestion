package labo_gestion_api.service;

import labo_gestion_api.model.SalleTp;
import labo_gestion_api.model.User;
import labo_gestion_api.repository.SalleTpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor  // ✅ أفضل من @Autowired
public class SalleTpService {

    private final SalleTpRepository salleTpRepository;
    private final UserService userService;  // ✅ أضف هذا

    public List<SalleTp> getAllSalleTps() {
        return salleTpRepository.findAll();
    }

    public SalleTp getSalleTpById(Long id) {
        return salleTpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalleTp not found with id " + id));
    }

    // ✅ جلب جميع القاعات للمدرسة الحالية
    public List<SalleTp> getSalleTpsForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return salleTpRepository.findByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ جلب القاعات مع الخزائن للمدرسة الحالية
    public List<SalleTp> getSalleTpsWithArmoiresForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return salleTpRepository.findSallesWithArmoiresByEcoleId(currentUser.getEcole().getId());
    }

    // ✅ إضافة قاعة جديدة للمدرسة الحالية
    public SalleTp createSalleTpForCurrentSchool(SalleTp salleTp, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }

        // التحقق من عدم وجود رقم مكرر لنفس المدرسة
        if (salleTp.getNumero() != null && !salleTp.getNumero().isEmpty()) {
            boolean exists = salleTpRepository.existsByNumeroAndEcoleId(
                    salleTp.getNumero(),
                    currentUser.getEcole().getId()
            );
            if (exists) {
                throw new RuntimeException("SalleTp with numero " + salleTp.getNumero() + " already exists in this school");
            }
        }

        salleTp.setEcole(currentUser.getEcole());  // ✅ ربط القاعة بالمدرسة
        return salleTpRepository.save(salleTp);
    }

    public SalleTp createSalleTp(SalleTp salleTp) {
        if (salleTp.getNumero() != null && !salleTp.getNumero().isEmpty()) {
            boolean exists = salleTpRepository.existsByNumero(salleTp.getNumero());
            if (exists) {
                throw new RuntimeException("SalleTp with numero " + salleTp.getNumero() + " already exists");
            }
        }
        return salleTpRepository.save(salleTp);
    }

    public SalleTp updateSalleTp(Long id, SalleTp salleTp) {
        SalleTp existingSalleTp = salleTpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalleTp not found with id " + id));

        if (salleTp.getNumero() != null && !salleTp.getNumero().isEmpty()) {
            if (!existingSalleTp.getNumero().equals(salleTp.getNumero())) {
                boolean exists = salleTpRepository.existsByNumero(salleTp.getNumero());
                if (exists) {
                    throw new RuntimeException("SalleTp with numero " + salleTp.getNumero() + " already exists");
                }
            }
            existingSalleTp.setNumero(salleTp.getNumero());
        }

        return salleTpRepository.save(existingSalleTp);
    }

    public void deleteSalleTp(Long id) {
        SalleTp salleTp = getSalleTpById(id);
        salleTpRepository.deleteById(id);
    }
}