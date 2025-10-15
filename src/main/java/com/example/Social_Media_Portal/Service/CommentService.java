package com.example.Social_Media_Portal.Service;

import com.example.Social_Media_Portal.DTO.CommentCreationDTO;
import com.example.Social_Media_Portal.DTO.CommentDTO2;
import com.example.Social_Media_Portal.DTO.CommentUpdateDTO;
import com.example.Social_Media_Portal.Entity.Comment;
import com.example.Social_Media_Portal.Entity.Post;
import com.example.Social_Media_Portal.Constants.Status;
import com.example.Social_Media_Portal.Entity.User;
import com.example.Social_Media_Portal.Exception.*;
import com.example.Social_Media_Portal.Repository.CommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    CommentRepository commentRepository;
    UserService userService;
    PostService postService;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserService userService, PostService postService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postService = postService;
    }



    @Transactional
    public CommentDTO2 createComment(CommentCreationDTO commentCreationDTO, String currentEmail) {
        User currentUser = userService.getUserByEmail(currentEmail);

        Post post = postService.findByPostId(commentCreationDTO.getPostId());

        if (post.getStatus() != Status.APPROVED) {
            throw new UnauthorizedActionException("Cannot comment on posts that are not approved");
        }
        Comment comment = new Comment();
        comment.setContent(commentCreationDTO.getContent());
        comment.setPost(post);
        comment.setUser(currentUser);
        comment.setStatus(Status.PENDING);
        comment.setDateTime(LocalDateTime.now());
        commentRepository.save(comment);

        return new CommentDTO2(
                comment.getCommentId(),
                comment.getContent(),
                comment.getDateTime(),
                comment.getStatus(),
                comment.getPost().getPostId(),
                comment.getUser().getUserId(),
                comment.getUser().getUsername()
        );
    }


    @Transactional
    public CommentDTO2 updateComment(CommentUpdateDTO commentUpdateDTO, Long commentId, String currentEmail) {
        User currentUser = userService.getUserByEmail(currentEmail);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: " + commentId));

        if (!comment.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedActionException("Comment cannot be modified by other user");
        }

        Post post = postService.findByPostId(commentUpdateDTO.getPostId());

        // Ensure the comment belongs to the specified post
        if (!comment.getPost().getPostId().equals(commentUpdateDTO.getPostId())) {
            throw new InvalidCommentPostAssociationException("Comment does not belong to the specified post");
        }
        if (post.getStatus() != Status.APPROVED) {
            throw new UnauthorizedActionException("Cannot update comments on posts that are not approved");
        }

        comment.setContent(commentUpdateDTO.getContent());
        comment.setStatus(Status.PENDING);
        commentRepository.save(comment);

        return new CommentDTO2(
                comment.getCommentId(),
                comment.getContent(),
                comment.getDateTime(),
                comment.getStatus(),
                comment.getPost().getPostId(),
                comment.getUser().getUserId(),
                comment.getUser().getUsername()
        );
    }


    public List<CommentDTO2> getUserComments(String currentEmail) {
        User currentUser = userService.getUserByEmail(currentEmail);

        if(commentRepository.findByUserId(currentUser.getUserId()).isEmpty()) {
            return Collections.emptyList();
        }

        List<Comment> comments = commentRepository.findByUserId(currentUser.getUserId());
        return comments.isEmpty() ? Collections.emptyList() : comments.stream()
                .map(comment -> new CommentDTO2(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getDateTime(),
                        comment.getStatus(),
                        comment.getPost().getPostId(),
                        comment.getUser().getUserId(),
                        comment.getUser().getUsername()
                ))
                .collect(Collectors.toList());
    }

    public ResponseEntity<String> deleteComment(Long commentId, String currentEmail) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()
                -> new CommentNotFoundException("Comment not present of given comment ID"));

        User loggedUser = userService.getUserByEmail(currentEmail);

        //current logged-in user saved in context == comment's user
        if(!(loggedUser.getEmail().equals(comment.getUser().getEmail()))) {
            throw new UnauthorizedActionException("User can only delete their own comments");
        }
        commentRepository.delete(comment);
        return ResponseEntity.ok("Comment deleted");
    }



    public String approveComments(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()
                -> new CommentNotFoundException("Comment ID not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if(email == null || email.isEmpty()) {
            throw new UserNotFoundException("No authenticated user found in security context");
        }

        User user = userService.getUserByEmail(email);

        if(comment.getUser().getUserId() != null && comment.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedActionException("Moderators cannot approve their own comments!");
        }
        comment.setStatus(Status.APPROVED);
        commentRepository.save(comment);
        return "Comment approved.";
    }

    public String rejectComments(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()
                -> new CommentNotFoundException("Comment ID not found"));

        comment.setStatus(Status.REJECTED);
        commentRepository.save(comment);
        return "Comment rejected.";
    }

    public List<CommentDTO2> getCommentsByStatus(String commentStatus) {
            String status1 = commentStatus.toUpperCase();
            List<Comment> comments = commentRepository.findByStatus(Status.valueOf(status1));  // One database call

            return comments.isEmpty() ? Collections.emptyList() :
                    comments.stream()
                            .map(comment -> new CommentDTO2(
                                    comment.getCommentId(),
                                    comment.getContent(),
                                    comment.getDateTime(),
                                    comment.getStatus(),
                                    comment.getPost().getPostId(),
                                    comment.getUser().getUserId(),
                                    comment.getUser().getUsername()

                            ))
                            .collect(Collectors.toList());

    }
}
