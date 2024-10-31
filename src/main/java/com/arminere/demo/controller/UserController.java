package com.arminere.demo.controller;
import com.arminere.demo.dto.UserRequest;
import com.arminere.demo.dto.UserResponse;
import com.arminere.demo.model.User;
import com.arminere.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.arminere.demo.dtomapper.UserMapper;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/user")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        try {
            // Log de entrada
            log.info("Creando usuario: {}", userRequest);

            User user = UserMapper.toEntity(userRequest);
            User createdUser = userService.save(user);

            if (createdUser == null) {
                log.error("El usuario creado es nulo");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            UserResponse userResponse = UserMapper.toResponse(createdUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);

        } catch (IllegalArgumentException e) {
            log.error("Error de argumento: {}", e.getMessage());
            UserResponse errorResponse = new UserResponse().setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);

        } catch (Exception e) {
            log.error("Ocurrió un error: {}", e.getMessage());
            UserResponse errorResponse = new UserResponse().setMessage("Ocurrió un error al procesar la solicitud.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }



    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestParam(required = false) String primerNombre,
            @RequestParam(required = false) String otrosNombres,
            @RequestParam(required = false) String primerApellido,
            @RequestParam(required = false) String segundoApellido,
            @RequestParam(required = false) String tipoIdentificacion,
            @RequestParam(required = false) String numeroIdentificacion,
            @RequestParam(required = false) String paisEmpleo,
            @RequestParam(required = false) String correoElectronico,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<UserResponse> users = userService.getUsers(primerNombre, otrosNombres, primerApellido, segundoApellido,
                tipoIdentificacion, numeroIdentificacion, paisEmpleo, correoElectronico, page, size);
        return ResponseEntity.ok(users);
    }



    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Validated UserRequest userRequest) {
        User user = UserMapper.toEntity(userRequest);
         return ResponseEntity.status(HttpStatus.OK)
                .body(UserMapper.toResponse(userService.updateUser(id, user)));
     }

}