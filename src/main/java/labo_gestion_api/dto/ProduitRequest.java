package labo_gestion_api.dto;

import labo_gestion_api.model.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProduitRequest {

    private String designation;
    private String reference;
    private ProduitType type;
    private LocalDate dateExp;
    private Categorie categorie;
    private Rubrique rubrique;
    private Durabilite durabilite;
    private Integer quantiteInitiale;
    private UniteMesure uniteMesure;
    private Long fournisseurId;   // ✅ هذا هو المفتاح
}
