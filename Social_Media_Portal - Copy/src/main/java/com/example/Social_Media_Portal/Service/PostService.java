package com.example.Social_Media_Portal.Service;

import com.example.Social_Media_Portal.DTO.PostCreationDTO;
import com.example.Social_Media_Portal.DTO.PostDTO;
import com.example.Social_Media_Portal.Entity.Post;
import com.example.Social_Media_Portal.Entity.Status;
import com.example.Social_Media_Portal.Entity.User;
import com.example.Social_Media_Portal.Repository.PostRepository;
import com.example.Social_Media_Portal.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;


//    public String createPost(PostCreationDTO postCreationDTO) {
//        User user = userRepository.findById(postCreationDTO.getUser_id()).get();
//        if(user==null) {
//            throw new RuntimeException("User not present to create post");
//        }
//        Post post = new Post();
//        post.setContent(postCreationDTO.getContent());
//        post.setStatus(Status.PENDING);
//        post.setDateTime(LocalDateTime.now());
//        post.setUser(user);
//        postRepository.save(post);
//        return "Post send for moderation!";
//    }

    @Transactional
    public PostDTO createPost(PostCreationDTO postCreationDTO, String currentEmail) {
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (!postCreationDTO.getUser_id().equals(currentUser.getUser_id())) {
            throw new RuntimeException("User cannot create posts for other users.");
        }
        User user = userRepository.findById(postCreationDTO.getUser_id())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + postCreationDTO.getUser_id()));

        Post post = new Post();
        post.setContent(postCreationDTO.getContent());
        post.setStatus(Status.PENDING);
        post.setDateTime(LocalDateTime.now());
        post.setUser(user);
        postRepository.save(post);
        return new PostDTO(
                post.getPost_id(),
                post.getContent(),
                post.getStatus(),
                post.getDateTime(),
                post.getUser().getUser_id()
        );
    }

//    public String updatePost(PostCreationDTO postCreationDTO, Long postId) {
//        User user = userRepository.findById(postCreationDTO.getUser_id()).get();
//        if(user==null) {
//            throw new RuntimeException("User not present to create post");
//        }
//        Post post = postRepository.findById(postId).orElseThrow();
//        post.setContent(postCreationDTO.getContent());
//        post.setStatus(Status.PENDING);
//        post.setDateTime(LocalDateTime.now());
//        post.setUser(user);
//        postRepository.save(post);
//        return "Post send for moderation!";
//    }

    @Transactional
    public PostDTO updatePost(PostCreationDTO postCreationDTO, Long postId, String currentEmail) {
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        if (!post.getUser().getUser_id().equals(postCreationDTO.getUser_id())) {
            throw new RuntimeException("Post does not belong to the specified user");
        }

        // Ensure the authenticated user is authorized (owns the post)
        if (!post.getUser().getUser_id().equals(currentUser.getUser_id())) {
            throw new RuntimeException("Unauthorized to update this post");
        }
        post.setContent(postCreationDTO.getContent());
        post.setStatus(Status.PENDING);
        postRepository.save(post);
        return new PostDTO(
                post.getPost_id(),
                post.getContent(),
                post.getStatus(),
                post.getDateTime(),
                post.getUser().getUser_id()
        );
    }


//    never send entity as response -> endless loop
//    public List<PostDTO> MyPosts(Long id) {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User u = userRepository.findById(id).get();
//        return u.getPostList().stream()
//                .map(post -> new PostDTO(
//                        post.getPost_id(),
//                        post.getContent(),
//                        post.getStatus(),
//                        post.getDateTime(),
//                        post.getUser().getUser_id()
//                )).collect(Collectors.toList());
//    }

    public List<PostDTO> getUserPosts(Long userId, String currentEmail) {
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (!userId.equals(currentUser.getUser_id())) {
            throw new RuntimeException("Unauthorized to view posts for this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return user.getPostList() != null ?
                user.getPostList().stream()
                        .map(post -> new PostDTO(
                                post.getPost_id(),
                                post.getContent(),
                                post.getStatus(),
                                post.getDateTime(),
                                post.getUser().getUser_id()
                        ))
                        .collect(Collectors.toList()) :
                Collections.emptyList();
    }

    @Transactional
    public void deletePost(Long postId, String currentEmail) {
        User currentUser = userRepository.findByEmail(currentEmail).orElseThrow(()
                -> new RuntimeException("No authenticated user present"));

        Post post = postRepository.findById(postId).orElseThrow(()
                -> new RuntimeException("No post present for given post ID"));

        //current logged in user saved in context == post's user
        if(currentUser.getEmail().equals(post.getUser().getEmail())) {
            postRepository.delete(post);
        }
        throw new RuntimeException("User can only delete their own posts");
    }
}
