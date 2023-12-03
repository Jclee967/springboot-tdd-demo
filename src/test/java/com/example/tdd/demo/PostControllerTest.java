package com.example.tdd.demo;

import com.example.tdd.demo.controller.PostController;
import com.example.tdd.demo.record.Post;
import com.example.tdd.demo.repo.PostRepository;
//import org.junit.Test;  // incompatible when using junit and jupiter tgt
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

}
