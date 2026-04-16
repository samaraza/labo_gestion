package labo_gestion_api.service;


import labo_gestion_api.model.Labo;
import labo_gestion_api.repository.LaboRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LaboService {

    @Autowired
    private LaboRepository laboRepository;

    public List<Labo> getAllLabos() {
        return laboRepository.findAll();
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public Labo getLaboById(Long id) {
        return laboRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Labo not found with id " + id));
    }

    // ✅ تصحيح: إزالة setId لأن id يتم توليده تلقائياً
    public Labo createLabo(Labo labo) {
        // id يتم توليده تلقائياً بواسطة JPA
        return laboRepository.save(labo);
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public Labo updateLabo(Long id, Labo labo) {
        // التأكد من وجود العنصر قبل التحديث
        Labo existingLabo = laboRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Labo not found with id " + id));

        // تحديث الحقول المطلوبة فقط
        if (labo.getLaboType() != null) {
            existingLabo.setLaboType(labo.getLaboType());
        }

        // ملاحظة: لا نقوم بتحديث id لأن id لا يتغير
        // لا نقوم بتحديث salleTps هنا لأن لها علاقة منفصلة

        return laboRepository.save(existingLabo);
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public void deleteLabo(Long id) {
        Labo labo = getLaboById(id);
        laboRepository.deleteById(id);
    }
}
