package labo_gestion_api.service;

import labo_gestion_api.model.Armoire;
import labo_gestion_api.model.User;
import labo_gestion_api.repository.ArmoireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor  // ✅ فقط هذا
public class ArmoireService {

    private final ArmoireRepository armoireRepository;  // ✅ final
    private final UserService userService;  // ✅ final

    public List<Armoire> findAll() {
        return armoireRepository.findAll();
    }

    public Armoire findById(Long id) {
        return armoireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Armoire not found with id " + id));
    }

    public List<Armoire> findArmoiresForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return armoireRepository.findByEcoleId(currentUser.getEcole().getId());
    }

    public List<Armoire> findArmoiresWithProduitsForCurrentSchool(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        return armoireRepository.findArmoiresWithProduitsByEcoleId(currentUser.getEcole().getId());
    }

    public Armoire saveForCurrentSchool(Armoire armoire, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser.getEcole() == null) {
            throw new RuntimeException("User has no school assigned");
        }
        armoire.setEcole(currentUser.getEcole());
        return armoireRepository.save(armoire);
    }

    public Armoire save(Armoire armoire) {
        return armoireRepository.save(armoire);
    }

    public void deleteById(Long id) {
        armoireRepository.deleteById(id);
    }
}