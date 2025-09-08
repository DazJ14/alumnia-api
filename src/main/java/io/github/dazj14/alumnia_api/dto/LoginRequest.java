package io.github.dazj14.alumnia_api.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String correo;
    private String password;
}
