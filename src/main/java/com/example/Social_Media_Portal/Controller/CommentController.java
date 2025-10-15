package com.example.Social_Media_Portal.Controller;


import com.example.Social_Media_Portal.DTO.*;
import com.example.Social_Media_Portal.Service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    @PostMapping("/create")
    public ResponseEntity<CommentDTO2> makeComment(@Valid @RequestBody CommentCreationDTO commentCreationDTO) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        CommentDTO2 createdComment = commentService.createComment(commentCreationDTO, currentEmail);
        return ResponseEntity.ok(createdComment);
    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    @PatchMapping("/update/{commentId}")
    public ResponseEntity<CommentDTO2> updateComment(@Valid @RequestBody CommentUpdateDTO commentUpdateDTO, @PathVariable Long commentId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        //commentID, postID, loggedIn User
        CommentDTO2 updatedComment = commentService.updateComment(commentUpdateDTO, commentId, currentEmail);
        return ResponseEntity.ok(updatedComment);
    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    @GetMapping("/MyComments")
    public ResponseEntity<List<CommentDTO2>> getUserComments() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CommentDTO2> comments = commentService.getUserComments(currentEmail);
        return ResponseEntity.ok(comments);
    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return commentService.deleteComment(commentId, currentEmail);
    }

}
