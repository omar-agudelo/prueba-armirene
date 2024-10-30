package com.arminere.demo.util;

import com.arminere.demo.exception.ExceptionApi;
import com.arminere.demo.model.User;
import com.arminere.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    private final UserRepository userRepository;

    @Autowired
    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateNewUser(User user) {
        userRepository.findByTipoIdentificacionAndNumeroIdentificacion(
                        user.getTipoIdentificacion(),
                        user.getNumeroIdentificacion())
                .ifPresent(existingUser -> {
                    throw new ExceptionApi(
                            "Ya existe un empleado con este tipo y número de identificación"
                    );
                });
    }
}