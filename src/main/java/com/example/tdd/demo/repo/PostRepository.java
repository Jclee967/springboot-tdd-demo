package com.example.tdd.demo.repo;

import com.example.tdd.demo.record.Post;
import org.springframework.data.repository.ListCrudRepository;

public interface PostRepository extends ListCrudRepository<Post, Integer> {

}
