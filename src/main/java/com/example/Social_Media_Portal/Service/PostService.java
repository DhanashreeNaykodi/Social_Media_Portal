package com.example.Social_Media_Portal.Service;

import com.example.Social_Media_Portal.DTO.CommentDTO2;
import com.example.Social_Media_Portal.DTO.PostCreationDTO;
import com.example.Social_Media_Portal.DTO.PostDTO;
import com.example.Social_Media_Portal.DTO.PostDTO2;
import com.example.Social_Media_Portal.Entity.Post;
import com.example.Social_Media_Portal.Constants.Status;
import com.example.Social_Media_Portal.Entity.User;
import com.example.Social_Media_Portal.Exception.PostNotFoundException;
import com.example.Social_Media_Portal.Exception.UnauthorizedActionException;
import com.example.Social_Media_Portal.Exception.UserNotFoundException;
import com.example.Social_Media_Portal.Repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    PostRepository postRepository;
    UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }



    public Page<PostDTO2> showFeed(Pageable pageable) {
        if(postRepository.findByStatus(Status.APPROVED,pageable).isEmpty()) {
            return Page.empty();
        }
        Page<Post> postPage = postRepository.findByStatus(Status.APPROVED, pageable);
        return postPage.map(post -> new PostDTO2(
                post.getPostId(),
                post.getContent(),
                post.getStatus(),
                post.getDateTime(),
                post.getUser().getUserId(),
                post.getUser().getUsername(),
                post.getCommentList() != null ? post.getCommentList()
                        .stream()
                        .filter(comment -> comment.getStatus() == Status.APPROVED)
                        .map(comment -> new CommentDTO2(
                                comment.getCommentId(),
                                comment.getContent(),
                                comment.getDateTime(),
                                comment.getStatus(),
                                comment.getUser().getUserId(),
                                comment.getPost().getPostId(),
                                comment.getUser().getUsername()
                        ))
                        .collect(Collectors.toList()) : Collections.emptyList()
        ));
    }


    @Transactional
    public PostDTO createPost(PostCreationDTO postCreationDTO, String currentEmail) {

        User currentUser = userService.getUserByEmail(currentEmail);

        Post post = new Post();
        post.setContent(postCreationDTO.getContent());
        post.setStatus(Status.PENDING);
        post.setDateTime(LocalDateTime.now());
        post.setUser(currentUser);
        postRepository.save(post);
        return new PostDTO(
                post.getPostId(),
                post.getContent(),
                post.getStatus(),
                post.getDateTime(),
                post.getUser().getUserId(),
                post.getUser().getUsername()
        );
    }

    @Transactional
    public PostDTO updatePost(PostCreationDTO postCreationDTO, Long postId, String currentEmail) {
        User currentUser = userService.getUserByEmail(currentEmail);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        // Ensure the authenticated user is authorized (owns the post)
        if (!post.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedActionException("Users can only update their own posts");
        }

        post.setContent(postCreationDTO.getContent());
        post.setStatus(Status.PENDING);
        postRepository.save(post);
        return new PostDTO(
                post.getPostId(),
                post.getContent(),
                post.getStatus(),
                post.getDateTime(),
                post.getUser().getUserId(),
                post.getUser().getUsername()
        );
    }


    public List<PostDTO> getUserPosts(String currentEmail) {
        User currentUser = userService.getUserByEmail(currentEmail);

        return currentUser.getPostList() != null ?
                currentUser.getPostList().stream()
                        .map(post -> new PostDTO(
                                post.getPostId(),
                                post.getContent(),
                                post.getStatus(),
                                post.getDateTime(),
                                post.getUser().getUserId(),
                                post.getUser().getUsername()
                        ))
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }

    @Transactional
    public void deletePost(Long postId, String currentEmail) {
        User currentUser = userService.getUserByEmail(currentEmail);

        Post post = postRepository.findById(postId).orElseThrow(()
                -> new PostNotFoundException("No post present for given post ID"));

        //current logged-in user saved in context == post's user
        if(!(currentUser.getEmail().equals(post.getUser().getEmail()))) {
            throw new UnauthorizedActionException("User can only delete their own posts");
        }
        postRepository.delete(post);
    }



    public List<PostDTO> getPostsByStatus(String postStatus) {

        String status1 = postStatus.toUpperCase();
        List<Post> posts = postRepository.findByStatus(Status.valueOf(status1));  // One database call

        return posts.isEmpty() ? Collections.emptyList() :
                postRepository.findByStatus(Status.valueOf(status1)).stream()
                        .map(post -> new PostDTO(
                            post.getPostId(),
                            post.getContent(),
                            post.getStatus(),
                            post.getDateTime(),
                            post.getUser().getUserId(),
                            post.getUser().getUsername())
                        )
                    .collect(Collectors.toList());
    }



    @Transactional
    public ResponseEntity<String> approvePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        // Get the authenticated user's email from JWT
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (email == null || email.isEmpty()) {
            throw new UserNotFoundException("No authenticated user found in security context");
        }

        // Fetch the user by email
        User user = userService.getUserByEmail(email);

        // Check if the moderator is trying to approve their own post
        if (post.getUser() != null && post.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedActionException("Moderators cannot approve their own posts!");
        }
        post.setStatus(Status.APPROVED);
        postRepository.save(post);
        return ResponseEntity.ok("Post approved.");
    }


    public String rejectPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new PostNotFoundException("Post ID not found"));
        post.setStatus(Status.REJECTED);

        //when post is blocked, block it is all comments
        post.getCommentList().forEach(comment -> comment.setStatus(Status.REJECTED));
        postRepository.save(post);
        return "Post rejected.";
    }





    //protected helper methods
    protected Post findByPostId (Long postId) {
        return postRepository.findById(postId).orElseThrow(()
                -> new PostNotFoundException("No post found"));
    }

}