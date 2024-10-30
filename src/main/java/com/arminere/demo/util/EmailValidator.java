package com.arminere.demo.util;

import com.arminere.demo.model.User;
import com.arminere.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailValidator {
    private final UserRepository userRepository;

    @Autowired
    public EmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateUniqueEmail(User user) {
        String baseEmail = generateBaseEmail(user);
        String domain = getDomainForCountry(user.getPais());
        return generateUniqueEmailWithDomain(baseEmail, domain);
    }

    private String generateBaseEmail(User user) {
        return user.getPrimerNombre().toLowerCase() + "." +
                user.getPrimerApellido().toLowerCase();
    }

    private String getDomainForCountry(String country) {
        return "Colombia".equalsIgnoreCase(country)
                ? "tuarmi.com.co"
                : "armirene.com.ve";
    }

    private String generateUniqueEmailWithDomain(String baseEmail, String domain) {
        String email = baseEmail + "@" + domain;
        int counter = 1;

        while (userRepository.findByCorreoElectronico(email).isPresent()) {
            email = baseEmail + "." + counter + "@" + domain;
            counter++;
        }

        return email;
    }
}