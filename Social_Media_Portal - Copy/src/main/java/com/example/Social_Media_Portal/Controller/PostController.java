package com.example.Social_Media_Portal.Controller;

import com.example.Social_Media_Portal.DTO.PostCreationDTO;
import com.example.Social_Media_Portal.DTO.PostDTO;
import com.example.Social_Media_Portal.Service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    PostService postService;

    @PostMapping("/create")
    public ResponseEntity<PostDTO> makePost(@Valid @RequestBody PostCreationDTO postCreationDTO) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        PostDTO createdPost = postService.createPost(postCreationDTO, currentEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PatchMapping("/update/{post_id}")
    public ResponseEntity<PostDTO> updatePost(@Valid @RequestBody PostCreationDTO postCreationDTO, @PathVariable Long post_id) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        PostDTO updatedPost = postService.updatePost(postCreationDTO, post_id, currentEmail);
        return ResponseEntity.ok(updatedPost);
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<List<PostDTO>> getUserPosts(@PathVariable Long user_id) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PostDTO> posts = postService.getUserPosts(user_id, currentEmail);
        return ResponseEntity.ok(posts);
    }


    @DeleteMapping("/{post_id}")
    public void deletePost (@PathVariable Long post_id) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName(); //was set in jwtFilter (when login)
        postService.deletePost(post_id, currentEmail);
    }

}
