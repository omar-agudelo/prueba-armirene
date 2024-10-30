package com.arminere.demo.controller;
import com.arminere.demo.dto.UserRequest;
import com.arminere.demo.dto.UserResponse;
import com.arminere.demo.model.User;
import com.arminere.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.arminere.demo.dtomapper.UserMapper;

import java.util.List;

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
            User user = UserMapper.toEntity(userRequest);
            User createdUser = userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(UserMapper.toResponse(createdUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new UserResponse().setMessage(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserResponse().setMessage("Ocurrió un error al procesar la solicitud."));
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