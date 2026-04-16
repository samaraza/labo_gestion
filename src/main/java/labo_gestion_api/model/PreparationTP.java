package labo_gestion_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "preparations_tp")
public class PreparationTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ تصحيح: استخدام Preparation من نفس الحزمة
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preparation_id", nullable = false)
    private Preparation preparation;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    // العلاقة مع Tp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tp_id")
    @JsonIgnore
    private Tp tp;

    // Helper methods
    public void setTp(Tp tp) {
        this.tp = tp;
    }

    public Tp getTp() {
        return tp;
    }

    public void setPreparation(Preparation preparation) {
        this.preparation = preparation;
    }

    public Preparation getPreparation() {
        return preparation;
    }
}