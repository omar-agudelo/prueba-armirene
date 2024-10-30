package com.arminere.demo.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@Data
@Accessors(chain = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private LocalDateTime FechaHoraRegistro;
    private boolean estado;
    private LocalDateTime fechaEdicion;
}