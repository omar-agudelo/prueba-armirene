package com.arminere.demo.dtomapper;


import com.arminere.demo.dto.UserRequest;
import com.arminere.demo.dto.UserResponse;
import com.arminere.demo.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class UserMapper {

    public static User toEntity(UserRequest userRequest) throws IllegalArgumentException {
        if (userRequest == null) {
            throw new IllegalArgumentException("Los datos del usuario no pueden ser nulos.");
        }
        if (!validarNombre(userRequest.getPrimerApellido()) || userRequest.getPrimerApellido().length() > 20) {
            throw new IllegalArgumentException("El primer apellido es requerido y debe tener máximo 20 letras (A-Z sin acentos ni Ñ).");
        }
        if (!validarNombre(userRequest.getSegundoApellido()) || userRequest.getSegundoApellido().length() > 20) {
            throw new IllegalArgumentException("El segundo apellido es requerido y debe tener máximo 20 letras (A-Z sin acentos ni Ñ).");
        }
        if (!validarNombre(userRequest.getPrimerNombre()) || userRequest.getPrimerNombre().length() > 20) {
            throw new IllegalArgumentException("El primer nombre es requerido y debe tener máximo 20 letras (A-Z sin acentos ni Ñ).");
        }
        if (userRequest.getOtrosNombres() != null && !validarOtrosNombres(userRequest.getOtrosNombres())) {
            throw new IllegalArgumentException("Los otros nombres deben tener un máximo de 50 letras y solo pueden contener letras y espacios.");
        }
        if (!userRequest.getPais().equals("Colombia") && !userRequest.getPais().equals("Estados Unidos")) {
            throw new IllegalArgumentException("El país debe ser Colombia o Estados Unidos.");
        }
        if (!validarTipoIdentificacion(userRequest.getTipoIdentificacion())) {
            throw new IllegalArgumentException("Tipo de identificación no válido.");
        }
        if (!validarNumeroIdentificacion(userRequest.getNumeroIdentificacion())) {
            throw new IllegalArgumentException("El número de identificación debe ser alfanumérico y tener un máximo de 20 caracteres.");
        }
        if (!validarFechaIngreso(userRequest.getFechaIngreso())) {
            throw new IllegalArgumentException("La fecha de ingreso no puede ser superior a la fecha actual ni debe ser más de un mes menor.");
        }
        if (!validarArea(userRequest.getArea())) {
            throw new IllegalArgumentException("El área debe ser una opción válida.");
        }
        String correo = generarCorreoElectronico(userRequest.getPrimerNombre(), userRequest.getSegundoApellido(), userRequest.getPais(), userRequest.getNumeroIdentificacion());
        return new User()
                .setTipoIdentificacion(userRequest.getTipoIdentificacion())
                .setNumeroIdentificacion(userRequest.getNumeroIdentificacion())
                .setPrimerNombre(userRequest.getPrimerNombre())
                .setPrimerApellido(userRequest.getPrimerApellido())
                .setSegundoApellido(userRequest.getSegundoApellido())
                .setOtrosNombres(userRequest.getOtrosNombres())
                .setPais(userRequest.getPais())
                .setFechaIngreso(userRequest.getFechaIngreso())
                .setArea(userRequest.getArea())
                .setEstado(true)
                .setFechaHoraRegistro(LocalDateTime.now())
                .setCorreoElectronico(correo);

    }


    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setTipoIdentificacion(user.getTipoIdentificacion());
        response.setNumeroIdentificacion(user.getNumeroIdentificacion());
        response.setPrimerNombre(user.getPrimerNombre());
        response.setPrimerApellido(user.getPrimerApellido());
        response.setSegundoApellido(user.getSegundoApellido());
        response.setOtrosNombres(user.getOtrosNombres());
        response.setPais(user.getPais());
        response.setFechaIngreso(user.getFechaIngreso());
        response.setArea(user.getArea());
        response.setCorreoElectronico(user.getCorreoElectronico());
        response.setFechaHoraRegistro(user.getFechaHoraRegistro());
        response.setEstado(user.isEstado());
        response.setFechaEdicion(user.getFechaEdicion());

        return response;
    }

    private static boolean validarNombre(String nombre) {
        return nombre != null && nombre.matches("[A-Z]{1,20}");
    }

    private static boolean validarOtrosNombres(String otrosNombres) {
        return otrosNombres.matches("[A-Z ]{0,50}");
    }

    private static boolean validarTipoIdentificacion(String tipoIdentificacion) {
        return true;
    }

    private static boolean validarNumeroIdentificacion(String numeroIdentificacion) {
        return numeroIdentificacion != null && numeroIdentificacion.matches("[A-Za-z0-9-]{1,20}");
    }

    private static boolean validarFechaIngreso(LocalDate fechaIngreso) {
        LocalDate fechaActual = LocalDate.now();
        if (fechaIngreso.isAfter(fechaActual)) {
            return false;
        }
        LocalDate fechaLimite = fechaActual.minusMonths(1);
        if (fechaIngreso.isBefore(fechaLimite)) {
            return false;
        }
        return true;
    }

    private static boolean validarArea(String area) {
        return true;
    }

    private static String generarCorreoElectronico(String primerNombre, String segundoApellido, String pais, String numeroIdentificacion) {
        String dominio = pais.equals("Colombia") ? "tuarmi.com.co" : "armirene.com.ve";
        String correo = String.format("%s.%s.%s@%s", primerNombre.toLowerCase(), segundoApellido.toLowerCase(), numeroIdentificacion, dominio);
        return correo;
    }
}