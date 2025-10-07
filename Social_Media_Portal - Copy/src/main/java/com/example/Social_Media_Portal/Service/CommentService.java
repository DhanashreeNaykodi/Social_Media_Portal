package com.example.Social_Media_Portal.Service;

import com.example.Social_Media_Portal.DTO.CommentCreationDTO;
import com.example.Social_Media_Portal.DTO.CommentDTO;
import com.example.Social_Media_Portal.DTO.CommentDTO2;
import com.example.Social_Media_Portal.Entity.Comment;
import com.example.Social_Media_Portal.Entity.Post;
import com.example.Social_Media_Portal.Entity.Status;
import com.example.Social_Media_Portal.Entity.User;
import com.example.Social_Media_Portal.Repository.CommentRepository;
import com.example.Social_Media_Portal.Repository.PostRepository;
import com.example.Social_Media_Portal.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

//    public String createComment(CommentCreationDTO commentCreationDTO) {
//        Post post = postRepository.findById(commentCreationDTO.getPost_id()).orElseThrow();
//        post.getUser().getEmail().equals()
//        if(post.getStatus().equals(Status.REJECTED)) {
//            return "Cannot comment on blocked Post";
//        }
//        Comment comment = new Comment();
//        comment.setContent(commentCreationDTO.getContent());
//        comment.setPost(post);
//        comment.setStatus(Status.PENDING);
//        comment.setDateTime(LocalDateTime.now());
//        commentRepository.save(comment);
//        return "Comment send for moderation!";
//    }

    @Transactional
    public CommentDTO2 createComment(CommentCreationDTO commentCreationDTO, String currentEmail) {
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (!commentCreationDTO.getUserId().equals(currentUser.getUser_id())) {
            throw new RuntimeException("Unauthorized to create comment for this user");
        }
        User user = userRepository.findById(commentCreationDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + commentCreationDTO.getUserId()));
        Post post = postRepository.findById(commentCreationDTO.getPost_id())
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + commentCreationDTO.getPost_id()));

        if (post.getStatus() != Status.APPROVED) {
            throw new RuntimeException("Cannot comment on posts that are not approved");
        }
        Comment comment = new Comment();
        comment.setContent(commentCreationDTO.getContent());
        comment.setPost(post);
        comment.setUser(user);
        comment.setStatus(Status.PENDING);
        comment.setDateTime(LocalDateTime.now());
        commentRepository.save(comment);

        return new CommentDTO2(
                comment.getComment_id(),
                comment.getContent(),
                comment.getDateTime(),
                comment.getStatus(),
                comment.getPost().getPost_id(),
                comment.getUser().getUser_id()
        );
    }

//    public String updatePost(CommentCreationDTO commentCreationDTO, Long commentId) {
//        Post post = postRepository.findById(commentCreationDTO.getPost_id()).orElseThrow();
//        Comment comment = commentRepository.findById(commentId).orElseThrow();
//        comment.setContent(commentCreationDTO.getContent());
//        comment.setStatus(Status.PENDING);
//        comment.setDateTime(LocalDateTime.now());
//        comment.setPost(post);
//        commentRepository.save(comment);
//        return "Comment send for moderation!";
//    }

    @Transactional
    public CommentDTO2 updateComment(CommentCreationDTO commentCreationDTO, Long commentId, String currentEmail) {
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));

        if (!comment.getUser().getUser_id().equals(commentCreationDTO.getUserId())) {
            throw new RuntimeException("Comment does not belong to the specified user");
        }
        Post post = postRepository.findById(commentCreationDTO.getPost_id())
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + commentCreationDTO.getPost_id()));

        // Ensure the comment belongs to the specified post
        if (!comment.getPost().getPost_id().equals(commentCreationDTO.getPost_id())) {
            throw new RuntimeException("Comment does not belong to the specified post");
        }
        if (post.getStatus() != Status.APPROVED) {
            throw new RuntimeException("Cannot update comments on posts that are not approved");
        }

        comment.setContent(commentCreationDTO.getContent());
        comment.setStatus(Status.PENDING);
        commentRepository.save(comment);

        return new CommentDTO2(
                comment.getComment_id(),
                comment.getContent(),
                comment.getDateTime(),
                comment.getStatus(),
                comment.getPost().getPost_id(),
                comment.getUser().getUser_id()
        );
    }

    public List<CommentDTO> CommentsByID(Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        return post.getCommentList().stream()
                .map(comment -> new CommentDTO(
                        comment.getComment_id(),
                        comment.getContent(),
                        comment.getDateTime(),
                        comment.getStatus(),
                        comment.getPost().getPost_id()
                )).collect(Collectors.toList());
    }


//    public List<CommentDTO> MyComments(Long userId) {
//        User user = userRepository.findById(userId).orElseThrow();
//        return user.getPostList() != null ?
//                user.getPostList().stream()
//                        .filter(post -> post.getCommentList() != null) // Avoid null comment lists
//                        .flatMap(post -> post.getCommentList().stream()) // Flatten comments from all posts
//                        .map(comment -> new CommentDTO( // Use correct DTO class
//                                comment.getComment_id(),
//                                comment.getContent(),
//                                comment.getDateTime(),
//                                comment.getStatus(),
//                                comment.getPost().getPost_id()
//                        ))
//                        .collect(Collectors.toList()) :
//                Collections.emptyList();
//    }

    public List<CommentDTO2> getUserComments(Long userId, String currentEmail) {
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        if (!userId.equals(currentUser.getUser_id())) {
            throw new RuntimeException("Unauthorized to view comments for this user");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return commentRepository.findByUserId(userId).stream()
                .map(comment -> new CommentDTO2(
                        comment.getComment_id(),
                        comment.getContent(),
                        comment.getDateTime(),
                        comment.getStatus(),
                        comment.getPost().getPost_id(),
                        comment.getUser().getUser_id()
                ))
                .collect(Collectors.toList());
    }

    public ResponseEntity<String> deleteComment(Long commentId, String currentEmail) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()
                -> new RuntimeException("Comment not present of given comment ID"));

        User loggedUser = userRepository.findByEmail(currentEmail).orElseThrow(() ->
                new RuntimeException("No authorized user present"));

        //current logged in user saved in context == comment's user
        if(loggedUser.getEmail().equals(comment.getUser().getEmail())) {
            commentRepository.delete(comment);
        }
        throw new RuntimeException("User can only delete their own comments");
    }
}
