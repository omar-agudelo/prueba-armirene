package com.arminere.demo.service.imp;

import com.arminere.demo.dto.UserResponse;
import com.arminere.demo.dtomapper.UserMapper;
import com.arminere.demo.exception.ExceptionApi;
import com.arminere.demo.model.User;
import com.arminere.demo.repository.UserRepository;
import com.arminere.demo.service.UserService;
import com.arminere.demo.util.EmailValidator;
import com.arminere.demo.util.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EmailValidator emailValidator;
    private final UserValidator userValidator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           EmailValidator emailValidator,
                           UserValidator userValidator) {
        this.userRepository = userRepository;
        this.emailValidator = emailValidator;
        this.userValidator = userValidator;
    }

    @Override
    @Transactional
    public User save(User user) {
        log.info("Inicio del proceso de guardado de usuario");
        log.debug("Validando nuevo usuario con tipoIdentificacion: {} y numeroIdentificacion: {}",
                user.getTipoIdentificacion(), user.getNumeroIdentificacion());
        userValidator.validateNewUser(user);

        log.debug("Generando correo electrónico único para el usuario: {}", user.getPrimerNombre());
        String email = emailValidator.generateUniqueEmail(user);
        user.setCorreoElectronico(email);
        user.setFechaHoraRegistro(LocalDateTime.now());
        user.setEstado(true);
        log.info("Usuario guardado exitosamente ");
        return userRepository.save(user);
    }

    @Override
    public List<UserResponse> getUsers(String primerNombre, String otrosNombres, String primerApellido,
                                       String segundoApellido, String tipoIdentificacion,
                                       String numeroIdentificacion, String paisEmpleo,
                                       String correoElectronico, int page, int size) {
        log.info("Iniciando el proceso de búsqueda de usuarios");
        log.debug("Parámetros de búsqueda recibidos - primerNombre: {}, otrosNombres: {}, primerApellido: {}, segundoApellido: {}, tipoIdentificacion: {}, numeroIdentificacion: {}, paisEmpleo: {}, correoElectronico: {}, página: {}, tamaño: {}",
                primerNombre, otrosNombres, primerApellido, segundoApellido, tipoIdentificacion, numeroIdentificacion, paisEmpleo, correoElectronico, page, size);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findByFilters(
                primerNombre, otrosNombres, primerApellido, segundoApellido,
                tipoIdentificacion, numeroIdentificacion, paisEmpleo, correoElectronico, pageRequest);
        log.info("Consulta de usuarios completada. Usuarios encontrados: {}, Total de páginas: {}", usersPage.getTotalElements(), usersPage.getTotalPages());
        return usersPage.getContent().stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }



    @Override
    @Transactional
    public User updateUser(Long id, User user) {
        log.info("Iniciando el proceso de actualización para el usuario con ID: {}", id);

        // Buscar el usuario existente en la base de datos
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            log.warn("Usuario no encontrado con ID: {}", id);
            throw new ExceptionApi("Usuario no encontrado con ID: " + id);
        }
        User existingUser = existingUserOpt.get();
        log.info("Usuario encontrado. Iniciando la actualización de campos.");

        // Validar cambios en nombres o apellidos para regenerar el correo electrónico
        boolean nombresCambiados = !existingUser.getPrimerNombre().equals(user.getPrimerNombre()) ||
                !existingUser.getSegundoApellido().equals(user.getSegundoApellido()) ||
                !existingUser.getOtrosNombres().equals(user.getOtrosNombres()) ||
                !existingUser.getPrimerApellido().equals(user.getPrimerApellido());

        if (nombresCambiados) {
            log.debug("Los nombres o apellidos han cambiado, se regenerará el correo electrónico.");
        }

        // Actualizar los campos del usuario
        existingUser.setPrimerNombre(user.getPrimerNombre());
        existingUser.setOtrosNombres(user.getOtrosNombres());
        existingUser.setPrimerApellido(user.getPrimerApellido());
        existingUser.setSegundoApellido(user.getSegundoApellido());
        existingUser.setTipoIdentificacion(user.getTipoIdentificacion());
        existingUser.setNumeroIdentificacion(user.getNumeroIdentificacion());
        existingUser.setPais(user.getPais());
        existingUser.setArea(user.getArea());
        existingUser.setFechaIngreso(user.getFechaIngreso());
        existingUser.setEstado(user.isEstado());
        log.debug("Campos del usuario actualizados: {}", existingUser);

        // Regenerar el correo electrónico si los nombres o apellidos han cambiado
        if (nombresCambiados) {
            String newEmail = generateEmail(existingUser);
            existingUser.setCorreoElectronico(newEmail);
            log.info("Correo electrónico regenerado: {}", newEmail);
        }

        // Actualizar la fecha de edición
        existingUser.setFechaEdicion(LocalDateTime.now());
        log.debug("Fecha de edición actualizada a: {}", existingUser.getFechaEdicion());

        // Guardar los cambios
        userRepository.save(existingUser);
        log.info("Usuario con ID: {} actualizado exitosamente.", id);

        return existingUser;
    }


    // Método para generar el correo electrónico
    private String generateEmail(User user) {
        String primerNombre = user.getPrimerNombre().toLowerCase();
        String segundoApellido = user.getSegundoApellido().toLowerCase();
        return primerNombre + "." + segundoApellido + "@armirene.com.co";
    }



}