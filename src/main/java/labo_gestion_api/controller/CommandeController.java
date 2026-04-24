package labo_gestion_api.controller;

import labo_gestion_api.model.Commande;
import labo_gestion_api.model.User;
import labo_gestion_api.repository.UserRepository;
import labo_gestion_api.service.CommandeService;
import labo_gestion_api.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;


import labo_gestion_api.dto.CommandeDTO;
import labo_gestion_api.dto.ProduitCommandeDTO;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/commandes")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;
    private final UserRepository userRepository;
    private final UserService userService;  // ✅ أضف هذا

    @GetMapping
    public ResponseEntity<List<CommandeDTO>> getAllCommandes() {
        List<Commande> commandes = commandeService.findAll();
        List<CommandeDTO> dtos = commandes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ✅ جلب جميع الطلبيات للمدرسة الحالية
    @GetMapping("/my-school")
    public ResponseEntity<List<CommandeDTO>> getCommandesForCurrentSchool(Authentication authentication) {
        List<Commande> commandes = commandeService.findCommandesForCurrentSchool(authentication);
        List<CommandeDTO> dtos = commandes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ✅ جلب الطلبيات مع التفاصيل للمدرسة الحالية
    @GetMapping("/my-school/with-details")
    public ResponseEntity<List<CommandeDTO>> getCommandesWithDetailsForCurrentSchool(Authentication authentication) {
        List<Commande> commandes = commandeService.findCommandesWithDetailsForCurrentSchool(authentication);
        List<CommandeDTO> dtos = commandes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ✅ جلب طلبية بالمعرف (String)
    @GetMapping("/{id}")
    public ResponseEntity<Commande> getCommandeById(@PathVariable String id) {
        Commande commande = commandeService.findById(id);
        return ResponseEntity.ok(commande);
    }

    // ✅ إنشاء طلبية جديدة للمدرسة الحالية
    @PostMapping("/my-school")
    public ResponseEntity<CommandeDTO> createCommandeForCurrentSchool(
            @RequestBody Commande commande,
            Authentication authentication) {

        if (commande.getUser() != null && commande.getUser().getId() != null) {
            Integer userId = commande.getUser().getId();
            User fullUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            commande.setUser(fullUser);
        }

        Commande savedCommande = commandeService.saveForCurrentSchool(commande, authentication);
        CommandeDTO dto = convertToDTO(savedCommande);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<CommandeDTO> createCommande(@RequestBody Commande commande) {
        if (commande.getUser() != null && commande.getUser().getId() != null) {
            Integer userId = commande.getUser().getId();
            User fullUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            commande.setUser(fullUser);
        }
        Commande savedCommande = commandeService.save(commande);
        CommandeDTO dto = convertToDTO(savedCommande);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommandeDTO> updateCommande(
            @PathVariable String id,
            @RequestBody Commande updatedCommande) {

        Commande commande = commandeService.findById(id);

        if (updatedCommande.getDesignation() != null) {
            commande.setDesignation(updatedCommande.getDesignation());
        }
        if (updatedCommande.getDate() != null) {
            commande.setDate(updatedCommande.getDate());
        }
        if (updatedCommande.getObservation() != null) {
            commande.setObservation(updatedCommande.getObservation());
        }
        if (updatedCommande.getNumero() != null) {
            commande.setNumero(updatedCommande.getNumero());
        }
        if (updatedCommande.getFournisseur() != null) {
            commande.setFournisseur(updatedCommande.getFournisseur());
        }

        if (updatedCommande.getUser() != null && updatedCommande.getUser().getId() != null) {
            Integer directeurId = updatedCommande.getUser().getId();
            User directeur = userRepository.findById(directeurId)
                    .orElseThrow(() -> new RuntimeException("Directeur non trouvé avec id: " + directeurId));
            commande.setUser(directeur);
        }

        if (updatedCommande.getProduits() != null) {
            commande.getProduits().clear();
            for (var produit : updatedCommande.getProduits()) {
                commande.addProduit(produit);
            }
        }

        Commande updated = commandeService.save(commande);
        CommandeDTO dto = convertToDTO(updated);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable String id) {
        commandeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public void generateCommandePdf(
            @PathVariable String id,
            HttpServletResponse response) throws IOException {

        ByteArrayInputStream pdfStream = commandeService.generateCommandePdf(id);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=commande_" + id + ".pdf");
        response.getOutputStream().write(pdfStream.readAllBytes());
        response.getOutputStream().flush();
    }

    private CommandeDTO convertToDTO(Commande commande) {
        CommandeDTO dto = new CommandeDTO();
        dto.setId(commande.getId());
        dto.setDesignation(commande.getDesignation());
        dto.setDate(commande.getDate());
        dto.setObservation(commande.getObservation());
        dto.setNumero(commande.getNumero());

        if (commande.getFournisseur() != null) {
            dto.setFournisseurId(commande.getFournisseur().getId());
            dto.setFournisseurNom(commande.getFournisseur().getNom());
        }

        if (commande.getUser() != null) {
            dto.setUserId(commande.getUser().getId().longValue());
            dto.setUserFirstName(commande.getUser().getFirstname());
            dto.setUserLastName(commande.getUser().getLastname());
        }

        if (commande.getProduits() != null) {
            List<ProduitCommandeDTO> produitsDTO = commande.getProduits().stream()
                    .map(pc -> {
                        ProduitCommandeDTO pDto = new ProduitCommandeDTO();
                        pDto.setQuantiteAjoutee(pc.getQuantiteAjoutee());
                        if (pc.getProduit() != null) {
                            pDto.setProduitId(pc.getProduit().getId());
                            pDto.setProduitDesignation(pc.getProduit().getDesignation());
                        }
                        return pDto;
                    }).collect(Collectors.toList());
            dto.setProduits(produitsDTO);
        }

        return dto;
    }
}