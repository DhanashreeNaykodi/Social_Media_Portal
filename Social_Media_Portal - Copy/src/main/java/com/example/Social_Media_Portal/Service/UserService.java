package com.example.Social_Media_Portal.Service;
import com.example.Social_Media_Portal.DTO.CommentDTO2;
import com.example.Social_Media_Portal.DTO.PostDTO2;
import com.example.Social_Media_Portal.DTO.SignupDTO;
import com.example.Social_Media_Portal.DTO.UsersDTO;
import com.example.Social_Media_Portal.Entity.Role;
import com.example.Social_Media_Portal.Entity.Status;
import com.example.Social_Media_Portal.Entity.User;
import com.example.Social_Media_Portal.Repository.PostRepository;
import com.example.Social_Media_Portal.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

//    public List<UsersDTO> userProfile(Long id, String currentEmail) {
//        User user1 = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
//        User currentUser = userRepository.findByEmail(currentEmail).get();
//        if (currentUser == null) {
//            throw new RuntimeException("Unauthorized access");
//        }
//        if (!user1.getEmail().equals(currentUser) &&
//                !(currentUser.getRole() == Role.MODERATOR || currentUser.getRole() == Role.ADMIN)) {
//            throw new RuntimeException("Unauthorized access");
//        }
//        return userRepository.findById(id).stream()
//                .map(user -> new UsersDTO(
//                        user.getUser_id(),
//                        user.getUser_name(),
//                        user.getEmail(),
//                        user.isModerator(),
//                        user.getRole()
//                )).collect(Collectors.toList());
//    }

    public UsersDTO getUserProfile(Long id, String currentEmail) {
        User user1 = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        if (!user1.getEmail().equals(currentUser.getEmail()) &&
                !(currentUser.getRole() == Role.MODERATOR || currentUser.getRole() == Role.ADMIN)) {
            throw new RuntimeException("Unauthorized access");
        }
        return new UsersDTO(
                user1.getUser_id(),
                user1.getUser_name(),
                user1.getEmail(),
                user1.isModerator(),
                user1.getRole()
        );
    }

//    public String deleteUser(Long id) {
//        if(userRepository.findById(id).isEmpty()) {
//            throw new RuntimeException("No user present");
//        }
//        User u = userRepository.findById(id).get();
//        userRepository.delete(u);
//        return "User deleted successfully";
//    }

    @Transactional
    public void deleteUser(Long id, String currentEmail) {
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No user present with ID: " + id));
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
//        if (currentUser.getRole() != Role.ADMIN) {
//            throw new RuntimeException("Only admins can delete users");
//        }
        if(!currentUser.getEmail().equals(targetUser.getEmail())) {
            throw new RuntimeException("User cannot delete other user's account");
        }
        userRepository.delete(targetUser);
    }


    public List<PostDTO2> showFeed() {

        if(postRepository.findByStatus(Status.APPROVED).isEmpty()) {
            return Collections.emptyList();
        }
        else {
            return postRepository.findByStatus(Status.APPROVED).stream()
                    .map(post -> new PostDTO2(
                            post.getPost_id(),
                            post.getContent(),
                            post.getStatus(),
                            post.getDateTime(),
                            post.getUser().getUser_id(),
                            post.getCommentList()
                                    .stream().filter(comment -> comment.getStatus() == Status.APPROVED)
                                    .map(comment -> new CommentDTO2(
                                            comment.getComment_id(),
                                            comment.getContent(),
                                            comment.getDateTime(),
                                            comment.getStatus(),
                                            comment.getUser().getUser_id(),
                                            comment.getPost().getPost_id()
                                            )).collect(Collectors.toList())
                    ))
                    .collect(Collectors.toList());
        }
    }

    public ResponseEntity<String> updateUser(Long id, String currentEmail, SignupDTO signupDTO) {
        User targetUser = userRepository.findById(id).orElseThrow(()
                -> new RuntimeException("No user present with given ID : " + id));

        User currentUser = userRepository.findByEmail(currentEmail).orElseThrow(()
                -> new RuntimeException("Authenticated user not found"));

        if(!targetUser.getEmail().equals(currentUser.getEmail())) {
            throw new RuntimeException("User cannot update other user's profile.");
        }

        User user = new User();
        user.setEmail(signupDTO.getEmail());
        user.setUser_name(signupDTO.getUsername());
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
