package com.example.Social_Media_Portal.Controller;


import com.example.Social_Media_Portal.DTO.CommentDTO2;
import com.example.Social_Media_Portal.DTO.PostDTO;
import com.example.Social_Media_Portal.Service.CommentService;
import com.example.Social_Media_Portal.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moderator")
public class ModeratorController {

    PostService postService;
    CommentService commentService;

    @Autowired
    public ModeratorController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }


    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("posts/{postStatus}")
    public ResponseEntity<List<PostDTO>> getPostsByStatus (@PathVariable String postStatus) {
        return ResponseEntity.ok(postService.getPostsByStatus(postStatus));
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PatchMapping("posts/approve/{postId}")
    public ResponseEntity<String> approvePost (@PathVariable Long postId) {
        return postService.approvePost(postId);
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PatchMapping("posts/reject/{postId}")
    public ResponseEntity<String> rejectPost (@PathVariable Long postId) {
        return ResponseEntity.ok(postService.rejectPost(postId));
    }



    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("comments/{commentStatus}")
    public ResponseEntity<List<CommentDTO2>> getCommentsByStatus (@PathVariable String commentStatus) {
        return ResponseEntity.ok(commentService.getCommentsByStatus(commentStatus));
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PatchMapping("comments/approve/{commentId}")
    public ResponseEntity<String> approveComment (@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.approveComments(commentId));
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @PatchMapping("comments/reject/{commentId}")
    public ResponseEntity<String> rejectComment (@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.rejectComments(commentId));
    }

}
