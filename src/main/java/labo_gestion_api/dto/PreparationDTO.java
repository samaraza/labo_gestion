package labo_gestion_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import labo_gestion_api.model.Preparation;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PreparationDTO {

    private Long preparationId;      // بدلاً من Preparation preparation
    private String preparationDesignation;
    private Integer quantite;
}
