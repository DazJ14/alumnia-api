package io.github.dazj14.alumnia_api.service;

import io.github.dazj14.alumnia_api.dto.CreateUsuarioRequest;
import io.github.dazj14.alumnia_api.model.Alumno;
import io.github.dazj14.alumnia_api.model.Profesor;
import io.github.dazj14.alumnia_api.model.Usuario;
import io.github.dazj14.alumnia_api.repository.*;
import io.github.dazj14.alumnia_api.dto.UpdateUsuarioRequest;
import io.github.dazj14.alumnia_api.dto.UsuarioDto;
import io.github.dazj14.alumnia_api.repository.*;
import io.github.dazj14.alumnia_api.dto.ChangePasswordRequest;
import io.github.dazj14.alumnia_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.dazj14.alumnia_api.model.Admin; // <-- AÑADIR
import io.github.dazj14.alumnia_api.repository.AdminRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AlumnoRepository alumnoRepository;
    private final ProfesorRepository profesorRepository;
    private final AdminRepository adminRepository;
    private final RolRepository rolRepository;
    private final CarreraRepository carreraRepository;
    private final PlanEstudioRepository planEstudioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioDto> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::toUsuarioDto)
                .collect(Collectors.toList());
    }

    public UsuarioDto findById(Integer id) {
        return usuarioRepository.findById(id)
                .map(this::toUsuarioDto)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Transactional
    public UsuarioDto update(Integer id, UpdateUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Actualizar campos comunes
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setCorreo(request.getCorreo());

        // Actualizar campos específicos según el tipo de usuario
        if (usuario instanceof Alumno) {
            Alumno alumno = (Alumno) usuario;
            alumno.setMatricula(request.getMatricula());
        } else if (usuario instanceof Profesor) {
            Profesor profesor = (Profesor) usuario;
            profesor.setNumeroEmpleado(request.getNumeroEmpleado());
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return toUsuarioDto(usuarioActualizado);
    }

    public void deleteById(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public Usuario create(CreateUsuarioRequest request) {
        var rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        String rolNombre = rol.getNombreRol().toUpperCase(); // Usar mayúsculas para ser consistente

        switch (rolNombre) {
            case "ALUMNO":
                var carrera = carreraRepository.findById(request.getIdCarrera())
                        .orElseThrow(() -> new RuntimeException("Carrera no encontrada"));
                var planEstudio = planEstudioRepository.findById(request.getIdPlanEstudio())
                        .orElseThrow(() -> new RuntimeException("Plan de estudios no encontrado"));

                Alumno alumno = new Alumno();
                alumno.setNombre(request.getNombre());
                alumno.setApellido(request.getApellido());
                alumno.setCorreo(request.getCorreo());
                alumno.setPassword(passwordEncoder.encode(request.getPassword()));
                alumno.setFecha_creacion(LocalDateTime.now());
                alumno.setRol(rol);
                alumno.setMatricula(request.getMatricula());
                alumno.setCarrera(carrera);
                alumno.setPlanEstudio(planEstudio);
                return alumnoRepository.save(alumno);

            case "MAESTRO":
                Profesor profesor = new Profesor();
                profesor.setNombre(request.getNombre());
                profesor.setApellido(request.getApellido());
                profesor.setCorreo(request.getCorreo());
                profesor.setPassword(passwordEncoder.encode(request.getPassword()));
                profesor.setFecha_creacion(LocalDateTime.now());
                profesor.setRol(rol);
                profesor.setNumeroEmpleado(request.getNumeroEmpleado());
                profesor.setEspecialidad(request.getEspecialidad());
                return profesorRepository.save(profesor);

            case "ADMINISTRADOR": // <-- LÓGICA AÑADIDA
                Admin admin = new Admin();
                admin.setNombre(request.getNombre());
                admin.setApellido(request.getApellido());
                admin.setCorreo(request.getCorreo());
                admin.setPassword(passwordEncoder.encode(request.getPassword()));
                admin.setFecha_creacion(LocalDateTime.now());
                admin.setRol(rol);
                return adminRepository.save(admin);

            default:
                throw new IllegalArgumentException("Tipo de rol no soportado para la creación: " + rol.getNombreRol());
        }
    }

    private UsuarioDto toUsuarioDto(Usuario usuario) {
        UsuarioDto.UsuarioDtoBuilder builder = UsuarioDto.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .correo(usuario.getCorreo())
                .nombreRol(usuario.getRol().getNombreRol());

        if (usuario instanceof Alumno) {
            builder.matricula(((Alumno) usuario).getMatricula());
        } else if (usuario instanceof Profesor) {
            builder.numeroEmpleado(((Profesor) usuario).getNumeroEmpleado());
        }
        return builder.build();
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request, String userEmail) {
        // 1. Encontrar al usuario
        var user = usuarioRepository.findByCorreo(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // 2. Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("La contraseña actual es incorrecta.");
        }

        // 3. Verificar que la nueva contraseña y su confirmación coincidan
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("La nueva contraseña y la confirmación no coinciden.");
        }

        // 4. Encriptar y guardar la nueva contraseña
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(user);
    }
}