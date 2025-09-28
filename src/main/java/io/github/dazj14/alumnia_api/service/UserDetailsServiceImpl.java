package io.github.dazj14.alumnia_api.service;

import io.github.dazj14.alumnia_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + username));

        return toUserDetails(user);
    }

    public UserDetails loadUserById(Integer id) throws UsernameNotFoundException {
        var user = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con ID: " + id));

        return toUserDetails(user);
    }

    private UserDetails toUserDetails(io.github.dazj14.alumnia_api.model.Usuario user) {
        return User.builder()
                .username(user.getId().toString())
                .password(user.getPassword())
                .roles(user.getRol().getNombreRol())
                .build();
    }
}
