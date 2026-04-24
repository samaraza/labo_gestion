package labo_gestion_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
@Table(name = "preparations")
@EntityListeners(AuditingEntityListener.class)
public class Preparation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "designation", nullable = false, length = 200)
    private String designation;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit1_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Produit produit1;  // ✅ من نفس الحزمة

    @Column(name = "quantite1")
    private Integer quantite1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit2_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Produit produit2;  // ✅ من نفس الحزمة

    @Column(name = "quantite2")
    private Integer quantite2;

    @OneToMany(mappedBy = "preparation", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PreparationTP> preparationTPs = new ArrayList<>();

    // ✅ أضف هذا - ربط التحضير بمدرسة
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ecole_id")
    private Ecole ecole;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
}