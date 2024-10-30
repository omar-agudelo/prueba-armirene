package com.arminere.demo.service;

import com.arminere.demo.dto.UserResponse;
import com.arminere.demo.model.User;

import java.util.List;

public interface UserService {

    User save(User user);
    List<UserResponse> getUsers(String primerNombre, String otrosNombres, String primerApellido, String segundoApellido,
                                String tipoIdentificacion, String numeroIdentificacion, String paisEmpleo,
                                String correoElectronico, int page, int size);
    User updateUser(Long id, User user);

}
