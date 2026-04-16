package labo_gestion_api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PreparationRequest {

    private String designation;
    private LocalDateTime date;
    private Integer quantite;
    private Long produit1Id;
    private Integer quantite1;
    private Long produit2Id;
    private Integer quantite2;
}
