package io.github.dazj14.alumnia_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "administradores") // Se creará una nueva tabla 'administradores'
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Admin extends Usuario {
    // Esta clase puede estar vacía si el administrador
    // no tiene campos adicionales a los de Usuario.
}