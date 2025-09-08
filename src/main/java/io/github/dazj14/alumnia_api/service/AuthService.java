package io.github.dazj14.alumnia_api.service;

import io.github.dazj14.alumnia_api.dto.AuthResponse;
import io.github.dazj14.alumnia_api.dto.LoginRequest;
import io.github.dazj14.alumnia_api.repository.UsuarioRepository;
import io.github.dazj14.alumnia_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Inyección de dependencias por constructor de Lombok
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        var user = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new IllegalArgumentException("Correo o contraseña inválidos."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Correo o contraseña inválidos.");
        }

        // Creamos un UserDetails simple para pasar al generador de token
        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getCorreo())
                .password(user.getPassword())
                .roles(user.getRol().getNombreRol())
                .build();
        System.out.println(userDetails);

        var token = jwtService.generateToken(userDetails);
        return new AuthResponse(token, user.getRol().getNombreRol());
    }
}
