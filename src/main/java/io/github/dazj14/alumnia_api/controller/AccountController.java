package io.github.dazj14.alumnia_api.controller;

import io.github.dazj14.alumnia_api.dto.ChangePasswordRequest;
import io.github.dazj14.alumnia_api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final UsuarioService usuarioService;

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()") // Requiere que el usuario esté logueado, sin importar su rol
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request, Principal principal) {
        usuarioService.changePassword(request, principal.getName());
        return ResponseEntity.ok("Contraseña cambiada exitosamente.");
    }
}