package com.arminere.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String tipoIdentificacion;
    private String numeroIdentificacion;
    private String primerNombre;
    private String primerApellido;
    private String segundoApellido;
    private String otrosNombres;
    private String pais;
    private LocalDate fechaIngreso;
    private String area;
    private String correoElectronico;
    private LocalDateTime fechaHoraRegistro;
    private boolean estado;
    private LocalDateTime fechaEdicion;
    private String message;


    public UserResponse() {

    }
}
