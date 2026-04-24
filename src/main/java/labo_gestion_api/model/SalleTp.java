package labo_gestion_api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "salles_tp")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SalleTp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero", nullable = false, unique = true, length = 50)
    private String numero;

    // ✅ علاقة Many-to-One مع Labo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "labo_id")
    @JsonIgnore
    private Labo labo;

    // Relation One-to-Many avec Armoire
    @OneToMany(mappedBy = "salleTp", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Armoire> armoires = new ArrayList<>();

    // ✅ أضف هذا - ربط القاعة بمدرسة
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ecole_id")
    private Ecole ecole;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    // Helper methods pour Armoire
    public void addArmoire(Armoire armoire) {
        if (armoire != null) {
            armoires.add(armoire);
            armoire.setSalleTp(this);
        }
    }

    public void removeArmoire(Armoire armoire) {
        if (armoire != null) {
            armoires.remove(armoire);
            armoire.setSalleTp(null);
        }
    }

    public void clearArmoires() {
        armoires.forEach(armoire -> armoire.setSalleTp(null));
        armoires.clear();
    }

    // ✅ Helper methods pour Labo
    public void setLabo(Labo labo) {
        this.labo = labo;
    }

    public Labo getLabo() {
        return labo;
    }
}