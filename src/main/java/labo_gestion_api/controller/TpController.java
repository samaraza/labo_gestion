package labo_gestion_api.controller;

import labo_gestion_api.dto.TpDTO;
import labo_gestion_api.dto.PreparationDTO;
import labo_gestion_api.dto.ProduitDTO;
import labo_gestion_api.model.*;
import labo_gestion_api.service.TpService;
import labo_gestion_api.service.UserService;
import labo_gestion_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tps")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TpController {

    private final TpService tpService;
    private final UserService userService;
    private final SalleTpRepository salleTpRepository;
    private final PreparationRepository preparationRepository;
    private final ProduitRepository produitRepository;

    @GetMapping
    public ResponseEntity<List<TpDTO>> getAllTps() {
        List<Tp> tps = tpService.findAll();
        List<TpDTO> dtos = tps.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TpDTO> getTpById(@PathVariable Long id) {
        Tp tp = tpService.findById(id);
        return ResponseEntity.ok(convertToDTO(tp));
    }

    @PostMapping
    public ResponseEntity<TpDTO> createTp(@RequestBody TpDTO tpDto) {
        Tp tp = convertToEntity(tpDto);
        Tp savedTp = tpService.save(tp);
        return new ResponseEntity<>(convertToDTO(savedTp), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TpDTO> updateTp(@PathVariable Long id, @RequestBody TpDTO updatedTpDto) {
        Tp updatedTp = convertToEntity(updatedTpDto);
        Tp updated = tpService.update(id, updatedTp);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTp(@PathVariable Long id) {
        tpService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    // ================= CONVERSION ENTITY → DTO =================
    private TpDTO convertToDTO(Tp tp) {
        if (tp == null) return null;
        TpDTO dto = new TpDTO();
        dto.setId(tp.getId());

        if (tp.getJourTp() != null) {
            dto.setJourTp(LocalDate.parse(tp.getJourTp()));
        }

        dto.setNiveauScolaire(tp.getNiveauScolaire() != null ? tp.getNiveauScolaire().name() : null);
        dto.setType(tp.getType() != null ? tp.getType().name() : null);

        if (tp.getSalleTp() != null) {
            dto.setSalleTpId(tp.getSalleTp().getId());
            dto.setSalleTpNumero(tp.getSalleTp().getNumero());
        }
        if (tp.getProf() != null) {
            dto.setProfId(tp.getProf().getId().longValue());
            dto.setProfFirstName(tp.getProf().getFirstname());
            dto.setProfLastName(tp.getProf().getLastname());
        }

        // ✅ تعبئة التحضيرات
        if (tp.getPreparations() != null) {
            List<PreparationDTO> prepDTOs = new ArrayList<>();
            for (PreparationTP prepTP : tp.getPreparations()) {
                Preparation prep = prepTP.getPreparation();
                if (prep != null) {
                    PreparationDTO prepDto = new PreparationDTO();
                    prepDto.setPreparationId(prep.getId());
                    prepDto.setPreparationDesignation(prep.getDesignation());
                    prepDto.setQuantite(prepTP.getQuantite());
                    prepDTOs.add(prepDto);
                }
            }
            dto.setPreparations(prepDTOs);
        }

        // ✅ تعبئة المنتجات
        if (tp.getProduits() != null) {
            List<ProduitDTO> prodDTOs = new ArrayList<>();
            for (ProduitTP prodTP : tp.getProduits()) {
                Produit produit = prodTP.getProduit();
                if (produit != null) {
                    ProduitDTO prodDto = new ProduitDTO();
                    prodDto.setProduitId(produit.getId());
                    prodDto.setProduitDesignation(produit.getDesignation());
                    prodDto.setQuantite(prodTP.getQuantite());
                    prodDTOs.add(prodDto);
                }
            }
            dto.setProduits(prodDTOs);
        }
        return dto;
    }

    // ================= CONVERSION DTO → ENTITY =================
    private Tp convertToEntity(TpDTO dto) {
        if (dto == null) return null;
        Tp tp = new Tp();
        tp.setId(dto.getId());

        tp.setJourTp(dto.getJourTp() != null ? dto.getJourTp().toString() : null);

        if (dto.getNiveauScolaire() != null)
            tp.setNiveauScolaire(NiveauScolaire.valueOf(dto.getNiveauScolaire()));
        if (dto.getType() != null)
            tp.setType(TpType.valueOf(dto.getType()));

        if (dto.getProfId() != null) {
            User prof = userService.findById(dto.getProfId().intValue());
            tp.setProf(prof);
        }
        if (dto.getSalleTpId() != null) {
            SalleTp salle = salleTpRepository.findById(dto.getSalleTpId())
                    .orElseThrow(() -> new RuntimeException("Salle TP introuvable"));
            tp.setSalleTp(salle);
        }

        // ✅ تعبئة التحضيرات من DTO
        if (dto.getPreparations() != null) {
            for (PreparationDTO prepDto : dto.getPreparations()) {
                if (prepDto.getPreparationId() != null) {
                    Preparation prep = preparationRepository.findById(prepDto.getPreparationId())
                            .orElseThrow(() -> new RuntimeException("Preparation introuvable"));
                    PreparationTP prepTP = new PreparationTP();
                    prepTP.setPreparation(prep);
                    prepTP.setQuantite(prepDto.getQuantite());
                    tp.addPreparation(prepTP);
                }
            }
        }

        // ✅ تعبئة المنتجات من DTO
        if (dto.getProduits() != null) {
            for (ProduitDTO prodDto : dto.getProduits()) {
                if (prodDto.getProduitId() != null) {
                    Produit produit = produitRepository.findById(prodDto.getProduitId())
                            .orElseThrow(() -> new RuntimeException("Produit introuvable"));
                    ProduitTP prodTP = new ProduitTP();
                    prodTP.setProduit(produit);
                    prodTP.setQuantite(prodDto.getQuantite());
                    tp.addProduit(prodTP);
                }
            }
        }
        return tp;
    }


}