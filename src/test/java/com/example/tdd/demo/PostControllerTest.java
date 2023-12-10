package com.example.tdd.demo;

import com.example.tdd.demo.controller.PostController;
import com.example.tdd.demo.exception.PostNotFoundException;
import com.example.tdd.demo.record.Post;
import com.example.tdd.demo.repo.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// SpringBootTest is loading the whole application context, while webmvc only test the web layer by simulating HTTP requests
@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostRepository postRepository;
    List<Post> posts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        posts = List.of(
                new Post(1, 1, "Hello", "First Post.", null),
                new Post(2, 1, "Hello", "Second Post.", null));
    }

    @Test
    public void shouldFindAllPosts() throws Exception {
        String jsonResponse = """
                [
                    {
                        "id":1,
                        "userId":1,
                        "title":"Hello",
                        "body":"First Post.",
                        "version": null
                    },
                    {
                        "id":2,
                        "userId":1,
                        "title":"Hello",
                        "body":"Second Post.",
                        "version": null
                    }
                ]
                """;

        when(postRepository.findAll()).thenReturn(posts);
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

    }

    @Test
    public void shouldFindPost_whenGivenValidId() throws Exception {
        String jsonResponse = """
                    {
                        "id":1,
                        "userId":1,
                        "title":"Hello",
                        "body":"First Post.",
                        "version": null
                    }
                """;
        when(postRepository.findById(1)).thenReturn(Optional.of(posts.get(0)));

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    public void shouldNotFindPost_whenGivenInvalidId() throws Exception {
        when(postRepository.findById(999)).thenThrow(PostNotFoundException.class);

        mockMvc.perform(get("api/posts/999"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void shouldCreateNewPost_whenGivenValidPost() throws Exception {
        String postRequest = """
                    {
                        "id":3,
                        "userId":3,
                        "title":"Hello",
                        "body":"test",
                        "version": null
                    }
                """;

        Post newPost = new Post(3,3, "Hello","test",null);
        when(postRepository.save(newPost)).thenReturn(newPost);

        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content(postRequest))
                .andExpect(status().isCreated())
                .andExpect(content().json(postRequest));
    }

    @Test
    public void shouldNotCreateNewPost_whenGivenInValidPost() throws Exception {
        String postRequest = """
                    {
                        "id":3,
                        "userId":3,
                        "title":"",
                        "body":"",
                        "version": null
                    }
                """;

        Post newPost = new Post(3,3, "","",null);
        when(postRepository.save(newPost)).thenReturn(newPost);

        mockMvc.perform(post("/api/posts")
                        .contentType("application/json")
                        .content(postRequest))
                .andExpect(status().isBadRequest());
    }



    @Test
    void shouldUpdatePost_whenGivenValidPost() throws Exception {
        String postRequest = """
                    {
                        "id":1,
                        "userId":1,
                        "title":"New Title",
                        "body":"New body message",
                        "version": null
                    }
                """;

        Post updatedPost = new Post(1,1, "New Title","New body message",null);
        when(postRepository.findById(1)).thenReturn(Optional.of(posts.get(0)));
        when(postRepository.save(updatedPost)).thenReturn(updatedPost);

        mockMvc.perform(put("/api/posts/1")
                        .contentType("application/json")
                        .content(postRequest))
                .andExpect(status().isOk())
                .andExpect(content().json(postRequest));

    }

    @Test
    void shouldNotUpdatePost_whenGivenInvalidPost() throws Exception {
        String postRequest = """
                    {
                        "id":1,
                        "userId":1,
                        "title":"",
                        "body":"",
                        "version": null
                    }
                """;

        mockMvc.perform(put("/api/posts/1")
                        .contentType("application/json")
                        .content(postRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeletePost_whenGivenValidId() throws Exception {

        when(postRepository.findById(1)).thenReturn(Optional.of(posts.get(0)));
        doNothing().when(postRepository).delete(posts.get(0));
        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());
        verify(postRepository,times(1)).delete(posts.get(0));
    }

    @Test
    void shouldNotDeletePost_whenGivenInvalidId() throws Exception {

        when(postRepository.findById(999)).thenThrow(PostNotFoundException.class);

        mockMvc.perform(delete("/api/posts/999"))
                .andExpect(status().isNotFound());

    }

}
