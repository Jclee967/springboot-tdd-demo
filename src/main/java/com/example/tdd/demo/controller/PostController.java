package com.example.tdd.demo.controller;

import com.example.tdd.demo.exception.PostNotFoundException;
import com.example.tdd.demo.record.Post;
import com.example.tdd.demo.repo.PostRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    @GetMapping("")
    List<Post> findAll(){
        return postRepository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Post> findById(@PathVariable Integer id){
        return Optional.of(postRepository.findById(id)).orElseThrow(PostNotFoundException::new);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    Post create(@Valid @RequestBody Post post){
        return postRepository.save(post);
    }

    @PutMapping("/{id}")
    Post update(@PathVariable Integer id, @Valid @RequestBody Post post){
        Optional<Post> existingPost = postRepository.findById(id);
        if(existingPost.isPresent()){
            Post updatePost = new Post(
                    existingPost.get().id(),
                    existingPost.get().userId(),
                    post.title(),
                    post.body(),
                    existingPost.get().version()
            );

            return postRepository.save(updatePost);
        }else{
            throw new PostNotFoundException();
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    void delete(@PathVariable Integer id){
        Optional<Post> post = postRepository.findById(id);

        if(post.isPresent()){
            postRepository.delete(post.get());
        }else{
            throw new PostNotFoundException();
        }
    }

}
