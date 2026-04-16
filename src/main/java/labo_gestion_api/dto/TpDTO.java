package labo_gestion_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class TpDTO {

    private Long id;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate jourTp;
    private String niveauScolaire;
    private String type;
    private Long salleTpId;        // seulement l'ID de la salle, pas l'objet complet
    private Long profId;
    private String profFirstName;
    private String profLastName;
    private String salleTpNumero;


    private List<PreparationDTO> preparations;

    private List<ProduitDTO> produits;
}
