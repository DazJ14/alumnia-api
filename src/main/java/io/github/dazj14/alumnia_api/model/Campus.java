package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "campus")
public class Campus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_campus")
    private Integer id;

    @Column(name = "nombre_campus", nullable = false, length = 255)
    private String nombreCampus;

    private String estado;
    private String municipio;
    private String calle;
    @Column(name = "codigo_postal")
    private String codigoPostal;
}