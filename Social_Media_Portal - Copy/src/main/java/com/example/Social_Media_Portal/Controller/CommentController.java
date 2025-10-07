package com.example.Social_Media_Portal.Controller;


import com.example.Social_Media_Portal.DTO.*;
import com.example.Social_Media_Portal.Service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<CommentDTO2> makeComment(@Valid @RequestBody CommentCreationDTO commentCreationDTO) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        CommentDTO2 createdComment = commentService.createComment(commentCreationDTO, currentEmail);
        return ResponseEntity.ok(createdComment);
    }

    @PatchMapping("/update/{comment_id}")
    public ResponseEntity<CommentDTO2> updateComment(@Valid @RequestBody CommentCreationDTO commentCreationDTO, @PathVariable Long comment_id) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        CommentDTO2 updatedComment = commentService.updateComment(commentCreationDTO, comment_id, currentEmail);
        return ResponseEntity.ok(updatedComment);
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<List<CommentDTO2>> getUserComments(@PathVariable Long user_id) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CommentDTO2> comments = commentService.getUserComments(user_id, currentEmail);
        return ResponseEntity.ok(comments);
    }


    @DeleteMapping("/{comment_id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long comment_id) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return commentService.deleteComment(comment_id, currentEmail);
    }

}
