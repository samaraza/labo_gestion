package labo_gestion_api.service;

import labo_gestion_api.model.Armoire;
import labo_gestion_api.repository.ArmoireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ArmoireService {

    @Autowired
    private ArmoireRepository armoireRepository;


    // ✅ تغيير: findAll() لا يحتاج تغيير
    public List<Armoire> findAll() {
        return armoireRepository.findAll();
    }

    // ✅ تغيير: المعامل من String إلى Long
    public Armoire findById(Long id) {
        return armoireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Armoire not found with id " + id));
    }

    // ✅ save() لا يحتاج تغيير
    public Armoire save(Armoire armoire) {
        return armoireRepository.save(armoire);
    }

    // ✅ تغيير: المعامل من String إلى Long
    public void deleteById(Long id) {
        armoireRepository.deleteById(id);
    }
}
