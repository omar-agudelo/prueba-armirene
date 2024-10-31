package com.arminere.demo;

import com.arminere.demo.controller.UserController;
import com.arminere.demo.dto.UserRequest;
import com.arminere.demo.dto.UserResponse;
import com.arminere.demo.dtomapper.UserMapper;
import com.arminere.demo.model.User;
import com.arminere.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class DemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserRequest userRequest;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        userRequest = new UserRequest()
                .setPrimerApellido("ROJAS")
                .setSegundoApellido("PEREZ")
                .setPrimerNombre("ANDRES")
                .setOtrosNombres("CARLOS")
                .setPais("Colombia")
                .setTipoIdentificacion("Cédula de Ciudadanía")
                .setNumeroIdentificacion("12345679")
                .setFechaIngreso(LocalDate.now())
                .setArea("Talento Humano");
    }

    @Test
    void createUser() throws Exception {
        User createdUser = new User()
                .setId(1L)
                .setTipoIdentificacion("Cédula de Ciudadanía")
                .setNumeroIdentificacion("12345679")
                .setPrimerNombre("ANDRES")
                .setPrimerApellido("ROJAS")
                .setSegundoApellido("PEREZ")
                .setOtrosNombres("CARLOS")
                .setPais("Colombia")
                .setFechaIngreso(LocalDate.now())
                .setArea("Talento Humano")
                .setEstado(true)
                .setFechaHoraRegistro(LocalDateTime.now())
                .setCorreoElectronico("andres.perez@example.com");

        when(userService.save(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/v1/user") // Asegúrate que coincida con la ruta del controlador
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"primerApellido\":\"ROJAS\",\"segundoApellido\":\"PEREZ\",\"primerNombre\":\"ANDRES\",\"otrosNombres\":\"CARLOS\",\"pais\":\"Colombia\",\"tipoIdentificacion\":\"Cédula de Ciudadanía\",\"numeroIdentificacion\":\"12345679\",\"fechaIngreso\":\"" + LocalDate.now() + "\",\"area\":\"Talento Humano\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdUser.getId()));
    }

    @Test
    void getUsers_Success() throws Exception {
        UserResponse user1 = new UserResponse()
                .setId(1L)
                .setPrimerNombre("ANDRES")
                .setPrimerApellido("ROJAS")
                .setCorreoElectronico("andres.perez@example.com");

        UserResponse user2 = new UserResponse()
                .setId(2L)
                .setPrimerNombre("JUAN")
                .setPrimerApellido("PEREZ")
                .setCorreoElectronico("juan.perez@example.com");

        List<UserResponse> userResponses = Arrays.asList(user1, user2);

        // Mock the service response
        when(userService.getUsers(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
        )).thenReturn(userResponses);

        // Perform the GET request
        mockMvc.perform(get("/v1/user")
                        .param("primerNombre", "ANDRES")
                        .param("otrosNombres", "CARLOS ")
                        .param("primerApellido", "ROJAS")
                        .param("segundoApellido", "PEREZ")
                        .param("tipoIdentificacion", "Cédula de Ciudadanía")
                        .param("numeroIdentificacion", "12345678")
                        .param("paisEmpleo", "Colombia")
                        .param("correoElectronico", "juan.perez@armirene.com.co")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[0].primerNombre").value("ANDRES"))
                .andExpect(jsonPath("$[0].primerApellido").value("ROJAS"))
                .andExpect(jsonPath("$[0].correoElectronico").value("andres.perez@example.com"))
                .andExpect(jsonPath("$[1].id").value(user2.getId()))
                .andExpect(jsonPath("$[1].primerNombre").value("JUAN"))
                .andExpect(jsonPath("$[1].primerApellido").value("PEREZ"))
                .andExpect(jsonPath("$[1].correoElectronico").value("juan.perez@example.com"));
    }

    @Test
    void updateUser_Success() throws Exception {
        Long userId = 1L;
        User mockedUser = UserMapper.toEntity(userRequest);
        mockedUser.setId(userId);
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(mockedUser);

        mockMvc.perform(put("/v1/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.primerNombre").value("ANDRES"))
                .andExpect(jsonPath("$.primerApellido").value("ROJAS"))
                .andExpect(jsonPath("$.segundoApellido").value("PEREZ"))
                .andExpect(jsonPath("$.otrosNombres").value("CARLOS"))
                .andExpect(jsonPath("$.correoElectronico").value("andres.perez.12345679@tuarmi.com.co"))
                .andExpect(jsonPath("$.area").value("Talento Humano"));
    }


}
