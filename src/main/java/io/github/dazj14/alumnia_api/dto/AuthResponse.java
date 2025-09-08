package io.github.dazj14.alumnia_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Constructor con todos los argumentos
public class AuthResponse {
    private String token;
    private String rol;
}
