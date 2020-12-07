package com.degombo.videostore.controllers;

import com.degombo.videostore.models.dtos.AuthRequest;
import com.degombo.videostore.models.dtos.GenreDTO;
import com.degombo.videostore.models.dtos.JwtDTO;
import com.degombo.videostore.models.dtos.MovieDTO;
import com.degombo.videostore.models.entities.Movie;
import com.google.gson.Gson;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerTest {
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

    private List<Movie> findAll(String jwtToken) throws Exception {
        String response = mockMvc.perform(MockMvcRequestBuilders
                .get("/movies")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andReturn().getResponse().getContentAsString();

        return gson.fromJson(response, new TypeToken<List<Movie>>() {
        }.getType());
    }

    private void save(String jwtToken, MovieDTO movie) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/movies")
                .header("Authorization", "Bearer " + jwtToken)
                .content(gson.toJson(movie))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(201));
    }

    private ResultActions findById(String jwtToken, Long id) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .get("/movies/" + id)
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
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setTitle("It");
        movieDTO.setDescription("Scary movie");
        movieDTO.setGenres(Lists.newArrayList("HORROR")
                .stream()
                .map(GenreDTO::new)
                .collect(Collectors.toList()));

        save(jwtToken, movieDTO);

        boolean result = findAll(jwtToken).stream()
                .map(Movie::getTitle)
                .collect(Collectors.toList()).contains(movieDTO.getTitle());

        Assertions.assertTrue(result);
    }

    @Test
    public void updateExisting() throws Exception {
        String jwtToken = getFreshJWT();

        ResultActions result = findById(jwtToken, 1L);
        Movie movie = gson.fromJson(result.andReturn().getResponse()
                .getContentAsString(), Movie.class);
        Assertions.assertEquals("Home Alone", movie.getTitle());

        movie.setTitle("Cat in the Hat");

        mockMvc.perform(MockMvcRequestBuilders.put("/movies/1")
                .content(gson.toJson(movie))
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        Assertions.assertEquals("Cat in the Hat", movie.getTitle());

        movie.setTitle("Home Alone");

        mockMvc.perform(MockMvcRequestBuilders.put("/movies/1")
                .content(gson.toJson(movie))
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        Assertions.assertEquals("Home Alone", movie.getTitle());
    }

    @Test
    public void updateNonExisting() throws Exception {
        String jwtToken = getFreshJWT();

        Movie movie = new Movie();
        movie.setTitle("Split");
        movie.setGenres(Collections.emptySet());

        mockMvc.perform(MockMvcRequestBuilders.put("/movies/50")
                .content(gson.toJson(movie))
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(201));

        ResultActions resultActions = findById(jwtToken, 3L);
        Movie fromDB = gson.fromJson(resultActions.andReturn().getResponse().getContentAsString(), Movie.class);
        Assertions.assertEquals("Split", fromDB.getTitle());
    }

    @Test
    public void deleteById() throws Exception {
        String jwtToken = getFreshJWT();

        mockMvc.perform(MockMvcRequestBuilders.delete("/movies/4")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        Assertions.assertFalse(findAll(jwtToken).stream()
                .map(Movie::getTitle)
                .collect(Collectors.toList()).contains("Home Alone"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/movies/64")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(404));
    }

    @Test
    public void deleteAll() throws Exception {
        String jwtToken = getFreshJWT();

        Movie movie = new Movie();
        movie.setTitle("Home Alone");

        List<Movie> movies = findAll(jwtToken);

        mockMvc.perform(MockMvcRequestBuilders.delete("/movies")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));

        Assertions.assertEquals(Lists.emptyList(), findAll(jwtToken));
        movies.forEach(t -> {
            try {
                save(jwtToken, new MovieDTO(t.getTitle(), t.getDescription(), Collections.emptyList()));
            } catch (Exception ignored) {
            }
        });
    }


}