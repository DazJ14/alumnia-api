package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "semestre")
public class Semestre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSemestre;

    @Column(nullable = false)
    private String numSemestre;

    // Suponiendo una relación con periodo
    // @ManyToOne
    // @JoinColumn(name = "idperiodo")
    // private Periodo periodo;
}