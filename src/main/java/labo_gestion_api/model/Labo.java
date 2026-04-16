package labo_gestion_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "labos")
public class Labo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "labo_type", nullable = false, length = 50)
    private LaboType laboType;

    // ✅ علاقة One-to-Many مع SalleTp
    @OneToMany(mappedBy = "labo", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = true)
    //@JsonIgnore
    private List<SalleTp> salleTps = new ArrayList<>();

    // Helper methods
    public void addSalleTp(SalleTp salleTp) {
        if (salleTp != null) {
            salleTps.add(salleTp);
            salleTp.setLabo(this);
        }
    }

    public void removeSalleTp(SalleTp salleTp) {
        if (salleTp != null) {
            salleTps.remove(salleTp);
            salleTp.setLabo(null);
        }
    }

    public void clearSalleTps() {
        salleTps.forEach(salleTp -> salleTp.setLabo(null));
        salleTps.clear();
    }
}

enum LaboType {
    TECHNIQUE("Technique"),
    SCIENTIFIQUE("Scientifique"),
    INFORMATIQUE("Informatique"),
    PHYSIQUE("Physique");

    private final String displayName;

    LaboType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}