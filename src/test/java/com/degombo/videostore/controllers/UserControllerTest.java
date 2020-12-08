package com.degombo.videostore.controllers;

import com.degombo.videostore.models.dtos.AuthRequest;
import com.degombo.videostore.models.dtos.JwtDTO;
import com.degombo.videostore.models.dtos.UserDTO;
import com.google.gson.Gson;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.AfterTestExecution;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final Gson gson = new Gson();

    private String getFreshJWT() throws Exception {
        String s = mockMvc.perform(MockMvcRequestBuilders
                .post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new AuthRequest("bartol", "bartol"))))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return gson.fromJson(s, JwtDTO.class).getToken();
    }

    private List<UserDTO> findAll(String jwtToken) throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders
                .get("/users")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();

        return gson.fromJson(response, new TypeToken<List<UserDTO>>() {
        }.getType());
    }

    private void save(String jwtToken, UserDTO user) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .header("Authorization", "Bearer " + jwtToken)
                .content(gson.toJson(user))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(201));
    }

    private ResultActions findById(String jwtToken, Long id) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .get("/users/" + id)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    public void findAll() throws Exception {
        String jwtToken = getFreshJWT();
        findAll(jwtToken);
    }

    @Test
    public void findById() throws Exception {
        String jwtToken = getFreshJWT();
        findById(jwtToken, 1L)
                .andExpect(MockMvcResultMatchers.status().is(200));

        findById(jwtToken, 50L)
                .andExpect(MockMvcResultMatchers.status().is(404));
    }

    @Test
    public void save() throws Exception {
        String jwtToken = getFreshJWT();
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Mike");
        userDTO.setLastName("Tyson");
        userDTO.setUsername("mike");
        userDTO.setPassword("mike");
        save(jwtToken, userDTO);

        boolean result = findAll(jwtToken).stream()
                .map(UserDTO::getUsername)
                .collect(Collectors.toList()).contains(userDTO.getUsername());

        Assertions.assertTrue(result);
    }

    @Test
    public void updateExisting() throws Exception {
        String jwtToken = getFreshJWT();

        ResultActions result = findById(jwtToken, 1L);
        UserDTO user = gson.fromJson(result.andReturn().getResponse()
                .getContentAsString(), UserDTO.class);
        Assertions.assertEquals("Bartol", user.getFirstName());

        user.setFirstName("Peach");
        user.setLastName("Apple");
        user.setUsername("bartol");
        user.setPassword("strongPass");

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(gson.toJson(user))
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        Assertions.assertEquals("Peach",
                user.getFirstName());

        user.setFirstName("Bartol");
        user.setLastName("Bilic");
        user.setUsername("bartol");
        user.setPassword("bartol");

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                .content(gson.toJson(user))
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        Assertions.assertEquals("Bartol",
                user.getFirstName());
    }

    @Test
    public void updateNonExisting() throws Exception {
        String jwtToken = getFreshJWT();

        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Mike");
        userDTO.setLastName("Myers");
        userDTO.setUsername("mike");
        userDTO.setPassword("mike");

        mockMvc.perform(MockMvcRequestBuilders.put("/users/50")
                .content(gson.toJson(userDTO))
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(201));

        ResultActions resultActions = findById(jwtToken, 2L);
        UserDTO fromDB = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(), UserDTO.class);
        Assertions.assertEquals("Mike", fromDB.getFirstName());
    }

    @Test
    public void deleteById() throws Exception {
        String jwtToken = getFreshJWT();

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/3")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        Assertions.assertFalse(findAll(jwtToken).stream()
                .map(UserDTO::getLastName)
                .collect(Collectors.toList()).contains("Tyson"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/movies/64")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(404));
    }

    @Test
    @Disabled
    public void deleteAll() throws Exception {
        String jwtToken = getFreshJWT();

        mockMvc.perform(MockMvcRequestBuilders.delete("/users")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        Assertions.assertEquals(Lists.emptyList(), findAll(jwtToken));
    }
}