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
public class Submitter extends AbstractEntity {
    
    @Column(name = "name")
    private String name;
    
    @OneToMany(mappedBy = "submitter")
    private Set<Trade> trades = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Submitter submitter && Objects.equals(this.getId(), submitter.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }
}
