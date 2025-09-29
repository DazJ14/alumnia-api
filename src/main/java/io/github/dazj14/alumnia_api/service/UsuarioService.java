package io.github.dazj14.alumnia_api.service;

import io.github.dazj14.alumnia_api.dto.*;
import io.github.dazj14.alumnia_api.model.Alumno;
import io.github.dazj14.alumnia_api.model.Profesor;
import io.github.dazj14.alumnia_api.model.Usuario;
import io.github.dazj14.alumnia_api.repository.*;
import io.github.dazj14.alumnia_api.repository.*;
import io.github.dazj14.alumnia_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.dazj14.alumnia_api.model.Admin; // <-- AÑADIR
import io.github.dazj14.alumnia_api.repository.AdminRepository;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
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
        if (usuario instanceof Alumno alumno) {
            alumno.setMatricula(request.getMatricula());
        } else if (usuario instanceof Profesor profesor) {
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
    public CreateUsuarioResponse create(CreateUsuarioRequest request) {
        var rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));

        String rolNombre = rol.getNombreRol().toUpperCase();

        // --- LÓGICA DE GENERACIÓN DE CORREO Y CONTRASEÑA ---
        String correo = generarCorreoUnico(request.getNombre(), request.getApellido(), rolNombre);
        String passwordTemporal = generarPasswordTemporal();

        switch (rolNombre) {
            case "ALUMNO":
                var carrera = carreraRepository.findById(request.getIdCarrera())
                        .orElseThrow(() -> new ResourceNotFoundException("Carrera no encontrada"));
                var planEstudio = planEstudioRepository.findById(request.getIdPlanEstudio())
                        .orElseThrow(() -> new ResourceNotFoundException("Plan de estudios no encontrado"));

                Alumno alumno = new Alumno();
                alumno.setNombre(request.getNombre());
                alumno.setApellido(request.getApellido());
                alumno.setRol(rol);
                alumno.setFecha_creacion(LocalDateTime.now());
                alumno.setCarrera(carrera);
                alumno.setPlanEstudio(planEstudio);

                // Generar y asignar datos automáticos
                alumno.setMatricula(generarMatriculaUnica());
                alumno.setCorreo(correo);
                alumno.setPassword(passwordEncoder.encode(passwordTemporal));

                Alumno alumnoGuardado = alumnoRepository.save(alumno);

                // Construir y devolver el DTO de respuesta
                return CreateUsuarioResponse.builder()
                        .nombre(alumnoGuardado.getNombre())
                        .apellido(alumnoGuardado.getApellido())
                        .correo(alumnoGuardado.getCorreo())
                        .passwordTemporal(passwordTemporal) // Devolvemos la contraseña sin encriptar
                        .matricula(alumnoGuardado.getMatricula())
                        .build();

            case "MAESTRO":
                Profesor profesor = new Profesor();
                profesor.setNombre(request.getNombre());
                profesor.setApellido(request.getApellido());
                profesor.setRol(rol);
                profesor.setFecha_creacion(LocalDateTime.now());
                profesor.setEspecialidad(request.getEspecialidad());

                // Generar y asignar datos automáticos
                profesor.setNumeroEmpleado(generarNumeroEmpleadoUnico());
                profesor.setCorreo(correo);
                profesor.setPassword(passwordEncoder.encode(passwordTemporal));

                Profesor profesorGuardado = profesorRepository.save(profesor);

                // Construir y devolver el DTO de respuesta
                return CreateUsuarioResponse.builder()
                        .nombre(profesorGuardado.getNombre())
                        .apellido(profesorGuardado.getApellido())
                        .correo(profesorGuardado.getCorreo())
                        .passwordTemporal(passwordTemporal)
                        .numeroEmpleado(profesorGuardado.getNumeroEmpleado())
                        .build();

            case "ADMINISTRADOR":
                Admin admin = new Admin();
                admin.setNombre(request.getNombre());
                admin.setApellido(request.getApellido());
                admin.setRol(rol);
                admin.setFecha_creacion(LocalDateTime.now());

                // Generar y asignar datos automáticos
                admin.setCorreo(correo);
                admin.setPassword(passwordEncoder.encode(passwordTemporal));

                Admin adminGuardado = adminRepository.save(admin);

                // Construir y devolver el DTO de respuesta
                return CreateUsuarioResponse.builder()
                        .nombre(adminGuardado.getNombre())
                        .apellido(adminGuardado.getApellido())
                        .correo(adminGuardado.getCorreo())
                        .passwordTemporal(passwordTemporal)
                        .build();

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

    private String generarCorreoUnico(String nombre, String apellido, String rol) {
        // Normaliza el nombre y apellido para quitar acentos y espacios.
        String base = Normalizer.normalize(nombre.split(" ")[0] + "." + apellido.split(" ")[0], Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase();

        String dominio = switch (rol) {
            case "ALUMNO" -> "@alumnos.edu";
            default -> "@universidad.edu";
        };

        String correoFinal = base + dominio;
        int contador = 1;
        // Si el correo ya existe, le añade un número hasta encontrar uno único.
        while (usuarioRepository.findByCorreo(correoFinal).isPresent()) {
            correoFinal = base + (contador++) + dominio;
        }
        return correoFinal;
    }

    private String generarMatriculaUnica() {
        String matricula;
        do {
            // Genera un número aleatorio de 9 dígitos
            long numero = ThreadLocalRandom.current().nextLong(100_000_000, 1_000_000_000);
            matricula = String.valueOf(numero);
        } while (alumnoRepository.findByMatricula(matricula).isPresent());
        return matricula;
    }

    private String generarNumeroEmpleadoUnico() {
        String numeroEmpleado;
        do {
            // Genera un número aleatorio de 6 dígitos
            long numero = ThreadLocalRandom.current().nextLong(100_000, 1_000_000);
            numeroEmpleado = String.valueOf(numero);
        } while (profesorRepository.findByNumeroEmpleado(numeroEmpleado).isPresent());
        return numeroEmpleado;
    }

    private String generarPasswordTemporal() {
        // Genera una contraseña aleatoria de 8 caracteres
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}