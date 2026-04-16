package labo_gestion_api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data

public class CommandeDTO {

    private Long id;
    private String designation;
    private LocalDateTime date;
    private String observation;
    private String numero;
    private Long fournisseurId;
    private String fournisseurNom;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private List<ProduitCommandeDTO> produits;
}
