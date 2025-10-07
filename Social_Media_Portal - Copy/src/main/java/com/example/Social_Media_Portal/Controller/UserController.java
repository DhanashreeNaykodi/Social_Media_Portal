package com.example.Social_Media_Portal.Controller;

import com.example.Social_Media_Portal.DTO.PostDTO2;
import com.example.Social_Media_Portal.DTO.SignupDTO;
import com.example.Social_Media_Portal.DTO.UsersDTO;
import com.example.Social_Media_Portal.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/feed")
    public ResponseEntity<List<PostDTO2>> feed () {
        List<PostDTO2> feed = userService.showFeed();
        return ResponseEntity.ok(feed);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UsersDTO> getUserProfile(@PathVariable Long id) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UsersDTO userProfile = userService.getUserProfile(id, currentEmail);
        return ResponseEntity.ok(userProfile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.deleteUser(id, currentEmail);
        return ResponseEntity.ok("User deleted");
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody SignupDTO signupDTO) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.updateUser(id, currentEmail, signupDTO);
    }
}
