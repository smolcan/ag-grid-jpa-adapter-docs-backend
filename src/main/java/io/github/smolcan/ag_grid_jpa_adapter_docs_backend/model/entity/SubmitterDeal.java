package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "submitter_deal")
@Getter
@Setter
public class SubmitterDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // For auto-increment behavior
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "submitterDeal")
    private Set<Trade> trades = new HashSet<>();


    @Override
    public boolean equals(Object obj) {
        return obj instanceof SubmitterDeal deal && Objects.equals(this.id, deal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
