package com.arminere.demo.repository;

import com.arminere.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTipoIdentificacionAndNumeroIdentificacion(String tipoIdentificacion, String numeroIdentificacion);

    Optional<User> findByCorreoElectronico(String correoElectronico);

    @Query("SELECT u FROM User u WHERE " +
            "(:primerNombre IS NULL OR u.primerNombre LIKE %:primerNombre%) AND " +
            "(:otrosNombres IS NULL OR u.otrosNombres LIKE %:otrosNombres%) AND " +
            "(:primerApellido IS NULL OR u.primerApellido LIKE %:primerApellido%) AND " +
            "(:segundoApellido IS NULL OR  u.segundoApellido LIKE %:segundoApellido% AND " +
            "(:tipoIdentificacion IS NULL OR u.tipoIdentificacion = :tipoIdentificacion) AND " +
            "(:numeroIdentificacion IS NULL OR u.numeroIdentificacion = :numeroIdentificacion) AND " +
            "(:paisEmpleo IS NULL OR u.pais = :paisEmpleo) AND " +
            "(:correoElectronico IS NULL OR u.correoElectronico LIKE %:correoElectronico%))")
    Page<User> findByFilters(String primerNombre, String otrosNombres, String primerApellido,
                             String segundoApellido, String tipoIdentificacion,
                             String numeroIdentificacion, String paisEmpleo,
                             String correoElectronico, Pageable pageable);
}
