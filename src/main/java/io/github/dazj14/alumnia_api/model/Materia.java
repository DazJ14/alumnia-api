package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Data
@Entity
@Table(name = "materias")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_materia")
    private Integer id;

    @Column(name = "clave_materia", nullable = false, unique = true, length = 50)
    private String claveMateria;

    @Column(name = "nombre_materia", nullable = false, length = 255)
    private String nombreMateria;

    @Column(nullable = false)
    private Integer creditos;

    // Relación para saber qué materias son requisitos para ESTA materia
    @ManyToMany
    @JoinTable(
            name = "materia_requisitos",
            joinColumns = @JoinColumn(name = "id_materia"),
            inverseJoinColumns = @JoinColumn(name = "id_materia_requisito")
    )
    @ToString.Exclude // Evita recursión infinita al generar el toString
    @EqualsAndHashCode.Exclude // Evita recursión infinita al comparar
    private Set<Materia> prerrequisitos;
}