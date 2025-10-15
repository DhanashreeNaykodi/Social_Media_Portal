package com.example.Social_Media_Portal.Controller;

import com.example.Social_Media_Portal.DTO.PostCreationDTO;
import com.example.Social_Media_Portal.DTO.PostDTO;
import com.example.Social_Media_Portal.DTO.PostDTO2;
import com.example.Social_Media_Portal.Service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    //    users/feed?page=1 for next page
    @GetMapping("/feed")
    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    public ResponseEntity<Page<PostDTO2>> feed (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "dateTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<PostDTO2> feed = postService.showFeed(pageable);
        return ResponseEntity.ok(feed);
    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    @PostMapping("/create")
    public ResponseEntity<PostDTO> makePost(@Valid @RequestBody PostCreationDTO postCreationDTO) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        PostDTO createdPost = postService.createPost(postCreationDTO, currentEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }


    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    @PatchMapping("/update/{postId}")
    public ResponseEntity<PostDTO> updatePost(@Valid @RequestBody PostCreationDTO postCreationDTO, @PathVariable Long postId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        PostDTO updatedPost = postService.updatePost(postCreationDTO, postId, currentEmail);
        return ResponseEntity.ok(updatedPost);
    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    @GetMapping("/myPosts")
    public ResponseEntity<List<PostDTO>> getUserPosts() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PostDTO> posts = postService.getUserPosts(currentEmail);
        return ResponseEntity.ok(posts);
    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost (@PathVariable Long postId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName(); //was set in jwtFilter (when login)
        postService.deletePost(postId, currentEmail);
        return ResponseEntity.ok("Post deleted");
    }

}
