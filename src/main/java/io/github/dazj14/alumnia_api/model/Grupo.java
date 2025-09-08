package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "grupos")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grupo")
    private Integer id;

    @Column(nullable = false)
    private Integer cupo;

    @Column(name = "codigo_grupo", nullable = false, unique = true)
    private String codigoGrupo;

    @ManyToOne
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    @ManyToOne
    @JoinColumn(name = "id_profesor", nullable = false)
    private Profesor profesor;

    @ManyToOne
    @JoinColumn(name = "id_periodo", nullable = false)
    private Periodo periodo;

    @ManyToOne
    @JoinColumn(name = "id_salon", nullable = false)
    private Salon salon;

    @ManyToOne
    @JoinColumn(name = "id_horario", nullable = false)
    private Horario horario;

    @ManyToOne
    @JoinColumn(name = "id_modalidad", nullable = false)
    private Modalidad modalidad;
}