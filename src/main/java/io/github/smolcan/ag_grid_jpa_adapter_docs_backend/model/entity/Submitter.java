package io.github.smolcan.ag_grid_jpa_adapter_docs_backend.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "submitter")
@Getter
@Setter
public class Submitter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // For auto-increment behavior
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @OneToMany(mappedBy = "submitter")
    private Set<Trade> trades = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Submitter submitter && Objects.equals(this.id, submitter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }
}
