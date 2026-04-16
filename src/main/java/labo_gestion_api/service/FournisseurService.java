package labo_gestion_api.service;

import labo_gestion_api.model.Fournisseur;
import labo_gestion_api.repository.FournisseurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FournisseurService {

    @Autowired
    private FournisseurRepository fournisseurRepository;

    public List<Fournisseur> findAll() {
        return fournisseurRepository.findAll();
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public Fournisseur findById(Long id) {
        return fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur not found with id " + id));
    }

    public Fournisseur save(Fournisseur fournisseur) {
        return fournisseurRepository.save(fournisseur);
    }

    // ✅ تصحيح: تغيير نوع id من String إلى Long
    public void deleteById(Long id) {
        Fournisseur fournisseur = findById(id);
        fournisseurRepository.deleteById(id);
    }
}
