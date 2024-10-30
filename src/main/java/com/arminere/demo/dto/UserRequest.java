package com.arminere.demo.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class UserRequest {

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




}