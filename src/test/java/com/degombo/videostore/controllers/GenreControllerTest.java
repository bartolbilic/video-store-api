package com.degombo.videostore.controllers;

import com.degombo.videostore.models.dtos.AuthRequest;
import com.degombo.videostore.models.dtos.JwtDTO;
import com.degombo.videostore.models.entities.Genre;
import com.degombo.videostore.repositories.GenreRepository;
import com.google.gson.Gson;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureMockMvc
class GenreControllerTest {

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

    private List<Genre> findAll(String jwtToken) throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders
                .get("/genres")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();

        return gson.fromJson(response, new TypeToken<List<Genre>>() {
        }.getType());
    }

    private void save(String jwtToken, Genre genre) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/genres")
                .header("Authorization", "Bearer " + jwtToken)
                .content(gson.toJson(genre))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(201));
    }

    private ResultActions findById(String jwtToken, Long id) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .get("/genres/" + id)
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
        Genre genre = new Genre();
        genre.setName("HORROR");

        save(jwtToken, genre);
        Assertions.assertTrue(findAll(jwtToken).stream().
                map(Genre::getName)
                .collect(Collectors.toList()).contains(genre.getName()));
    }

    @Test
    public void updateExisting() throws Exception {
        String jwtToken = getFreshJWT();

        ResultActions result = findById(jwtToken, 1L);
        Genre genre = gson.fromJson(result.andReturn().getResponse()
                .getContentAsString(), Genre.class);
        Assertions.assertEquals("COMEDY", genre.getName());

        genre.setName("MISTERY");

        mockMvc.perform(MockMvcRequestBuilders.put("/genres/1")
                .content(gson.toJson(genre))
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        Assertions.assertEquals("MISTERY", genre.getName());
    }

    @Test
    public void updateNonExisting() throws Exception {
        String jwtToken = getFreshJWT();

        Genre genre = new Genre();
        genre.setName("SCIENCE FICTION");

        mockMvc.perform(MockMvcRequestBuilders.put("/genres/50")
                .content(gson.toJson(genre))
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(201));

        Assertions.assertEquals("SCIENCE FICTION", genre.getName());
    }

    @Test
    public void deleteById() throws Exception {
        String jwtToken = getFreshJWT();

        mockMvc.perform(MockMvcRequestBuilders.delete("/genres/8")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        Assertions.assertFalse(findAll(jwtToken).stream()
                .map(Genre::getName)
                .collect(Collectors.toList()).contains("FAMILY"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/genres/64")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(404));
    }

    @Test
    public void deleteAll() throws Exception {
        String jwtToken = getFreshJWT();

        Genre genre = new Genre();
        genre.setName("SCIENCE FICTION");

        List<Genre> genres = findAll(jwtToken);

        mockMvc.perform(MockMvcRequestBuilders.delete("/genres")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        Assertions.assertEquals(Lists.emptyList(), findAll(jwtToken));
        genres.forEach(t -> {
            try {
                save(jwtToken, t);
            } catch (Exception ignored) {
            }
        });
    }
}