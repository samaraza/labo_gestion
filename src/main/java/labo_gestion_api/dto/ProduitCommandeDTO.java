package labo_gestion_api.dto;

import lombok.Data;

@Data
public class ProduitCommandeDTO {

    private Long produitId;
    private String produitDesignation;
    private Integer quantiteAjoutee;
}
