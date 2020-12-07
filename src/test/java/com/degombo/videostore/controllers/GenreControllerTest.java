package com.degombo.videostore.controllers;

import com.degombo.videostore.models.dtos.AuthRequest;
import com.degombo.videostore.models.dtos.JwtDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private String getFreshJWT() throws Exception {
        String s = mockMvc.perform(MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(new AuthRequest("bartol", "bartol"))))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return new ObjectMapper().readValue(s, JwtDTO.class).getToken();
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getAllGenres() throws Exception {
        String token = getFreshJWT();

        mockMvc.perform()

    }

}