package labo_gestion_api.service;


import labo_gestion_api.model.SalleTp;
import labo_gestion_api.repository.SalleTpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SalleTpService {

    @Autowired
    private SalleTpRepository salleTpRepository;

    public List<SalleTp> getAllSalleTps() {
        return salleTpRepository.findAll();
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public SalleTp getSalleTpById(Long id) {
        return salleTpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalleTp not found with id " + id));
    }

    // ✅ تصحيح: إنشاء ساعة TP جديدة
    public SalleTp createSalleTp(SalleTp salleTp) {
        // التحقق من عدم وجود رقم مكرر
        if (salleTp.getNumero() != null && !salleTp.getNumero().isEmpty()) {
            boolean exists = salleTpRepository.existsByNumero(salleTp.getNumero());
            if (exists) {
                throw new RuntimeException("SalleTp with numero " + salleTp.getNumero() + " already exists");
            }
        }
        return salleTpRepository.save(salleTp);
    }

    // ✅ تصحيح: تحديث ساعة TP
    public SalleTp updateSalleTp(Long id, SalleTp salleTp) {
        SalleTp existingSalleTp = salleTpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SalleTp not found with id " + id));

        // تحديث الرقم مع التحقق من عدم التكرار
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

    // ✅ تصحيح: حذف ساعة TP
    public void deleteSalleTp(Long id) {
        SalleTp salleTp = getSalleTpById(id);
        salleTpRepository.deleteById(id);
    }
}
