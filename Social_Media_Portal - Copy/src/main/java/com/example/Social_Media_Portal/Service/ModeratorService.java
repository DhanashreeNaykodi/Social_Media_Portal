package com.example.Social_Media_Portal.Service;

import com.example.Social_Media_Portal.DTO.CommentDTO;
import com.example.Social_Media_Portal.DTO.PostDTO;
import com.example.Social_Media_Portal.Entity.Comment;
import com.example.Social_Media_Portal.Entity.Post;
import com.example.Social_Media_Portal.Entity.Status;
import com.example.Social_Media_Portal.Entity.User;
import com.example.Social_Media_Portal.Repository.CommentRepository;
import com.example.Social_Media_Portal.Repository.PostRepository;
import com.example.Social_Media_Portal.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModeratorService {


    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;


    public List<PostDTO> PendingRequests() {

        if(postRepository.findByStatus(Status.PENDING).isEmpty()) {
            return Collections.emptyList();
        }
        else {
            return postRepository.findByStatus(Status.PENDING).stream()
                    .map(post -> new PostDTO(
                            post.getPost_id(),
                            post.getContent(),
                            post.getStatus(),
                            post.getDateTime(),
                            post.getUser().getUser_id()
                    ))
                    .collect(Collectors.toList());
        }
    }

    public List<PostDTO> ApprovedRequests() {
        if(postRepository.findByStatus(Status.APPROVED).isEmpty()) {
            return Collections.emptyList();
        }
        else {
            return postRepository.findByStatus(Status.APPROVED).stream()
                    .map(post -> new PostDTO(
                            post.getPost_id(),
                            post.getContent(),
                            post.getStatus(),
                            post.getDateTime(),
                            post.getUser().getUser_id()
                    ))
                    .collect(Collectors.toList());
        }
    }

    public List<PostDTO> RejectedRequests() {
        if(postRepository.findByStatus(Status.REJECTED).isEmpty()) {
            return Collections.emptyList();
        }
        else {
            return postRepository.findByStatus(Status.REJECTED).stream()
                    .map(post -> new PostDTO(
                            post.getPost_id(),
                            post.getContent(),
                            post.getStatus(),
                            post.getDateTime(),
                            post.getUser().getUser_id()
                    ))
                    .collect(Collectors.toList());
        }
    }
    private static final Logger logger = LoggerFactory.getLogger(ModeratorService.class);

    @Transactional
    public String approvePost(Long id) {
//        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Authenticated user not found"));
//        if(user != null && post.getUser().getUser_id().equals(user.getUser_id())) {
//            throw new RuntimeException("Moderators cannot approve their own posts!");
//        }
//        post.setStatus(Status.APPROVED);
        // wrong comments should not be approved.
//        post.getCommentList().forEach(comment -> comment.setStatus(Status.APPROVED));
//        postRepository.save(post);
//        return "Post approved.";

        // Fetch the post
        logger.info("Approving post with ID: {}", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + id));

        // Get the authenticated user's email from JWT
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("JWT Email: {}", email);

        if (email == null || email.isEmpty()) {
            logger.error("No authenticated user found in security context");
            throw new RuntimeException("No authenticated user found in security context");
        }

        // Fetch the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found for email: " + email));

        // Log user details
        logger.info("Authenticated user ID: {}, Post user ID: {}",
                user.getUser_id(), post.getUser() != null ? post.getUser().getUser_id() : null);

        // Check if the user is trying to approve their own post
        if (post.getUser() != null && post.getUser().getUser_id().equals(user.getUser_id())) {
            return "Moderators cannot approve their own posts!";
        }

        post.setStatus(Status.APPROVED);
        postRepository.save(post);
        logger.info("Post with ID {} approved successfully", id);
        return "Post approved.";
    }

    public String rejectPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setStatus(Status.REJECTED);

        //when post is blocked, block its all comments
        post.getCommentList().forEach(comment -> comment.setStatus(Status.REJECTED));
        postRepository.save(post);
        return "Post rejected.";
    }

    public List<CommentDTO> PendingComments() {
        if(commentRepository.findByStatus(Status.PENDING).isEmpty()) {
            return Collections.emptyList();
        }
        else {
            return commentRepository.findByStatus(Status.PENDING).stream()
                    .map(comment -> new CommentDTO(
                            comment.getComment_id(),
                            comment.getContent(),
                            comment.getDateTime(),
                            comment.getStatus(),
                            comment.getPost().getPost_id()
                    ))
                    .collect(Collectors.toList());
        }
    }

    public List<CommentDTO> ApprovedComments() {
        if(commentRepository.findByStatus(Status.APPROVED).isEmpty()) {
            return Collections.emptyList();
        }
        else {
            return commentRepository.findByStatus(Status.APPROVED).stream()
                    .map(comment -> new CommentDTO(
                            comment.getComment_id(),
                            comment.getContent(),
                            comment.getDateTime(),
                            comment.getStatus(),
                            comment.getPost().getPost_id()
                    ))
                    .collect(Collectors.toList());
        }
    }

    public List<CommentDTO> RejectedComments() {

        if(commentRepository.findByStatus(Status.REJECTED).isEmpty()) {
            return Collections.emptyList();
        }
        else {
            return commentRepository.findByStatus(Status.REJECTED).stream()
                    .map(comment -> new CommentDTO(
                            comment.getComment_id(),
                            comment.getContent(),
                            comment.getDateTime(),
                            comment.getStatus(),
                            comment.getPost().getPost_id()
                    ))
                    .collect(Collectors.toList());
        }
    }

    public String approveComments(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment ID not found"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        if(comment.getPost().getUser().getUser_id().equals(user.getUser_id())) {
            throw new RuntimeException("Moderators cannot approve their own comments!");
        }
        if(comment.getStatus().equals(Status.APPROVED)){
            return "Comment already approved";
        }
        comment.setStatus(Status.APPROVED);
        commentRepository.save(comment);
        return "Comment approved.";

    }

    public String rejectComments(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        comment.setStatus(Status.REJECTED);
        commentRepository.save(comment);
        return "Comment rejected.";
    }


}
