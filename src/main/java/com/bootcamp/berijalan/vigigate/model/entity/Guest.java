package com.bootcamp.berijalan.vigigate.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "guests")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 16)
    private String nik;

    @Column(nullable = false)
    private String nama;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @JsonIgnore
    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Visit> visits;
}
