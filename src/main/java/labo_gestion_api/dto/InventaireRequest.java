package labo_gestion_api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventaireRequest {

    private String anneeScolaire;
    private String commentaire;
    private LocalDateTime date;  // أو LocalDate حسب ما استخدمت
    private String responsable;
    private Long produitId;       // ← فقط الرقم التعريفي للمنتج
    private Integer quantiteRestante;
}
