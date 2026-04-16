package labo_gestion_api.dto;

import labo_gestion_api.model.Produit;
import lombok.Data;

@Data
public class ProduitDTO {

    private Long produitId;
    private String produitDesignation;
    private Integer quantite;
}
