package com.example.Social_Media_Portal.Controller;

import com.example.Social_Media_Portal.DTO.*;
import com.example.Social_Media_Portal.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> RegisterUser(@Valid @RequestBody SignupDTO signupDTO) {
        return userService.saveNewUser(signupDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        return userService.loginUser(loginDTO);
    }

    @GetMapping("/myProfile")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ResponseEntity<UsersDTO> getUserProfile() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserProfile(currentEmail);
    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUser() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.deleteUser(currentEmail);
        return ResponseEntity.ok("User deleted");
    }

//    @PreAuthorize("hasRole('MODERATOR')")
//    @DeleteMapping("/deleteUserById/{userId}")
//    public ResponseEntity<String> deleteUserById (@PathVariable Long userId) {
//        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//        userService.deleteUserById(currentEmail, userId);
//        return ResponseEntity.ok("User deleted");
//    }

    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    @PostMapping("/updateUser")
    public ResponseEntity<UsersDTO> updateUser(@Valid @RequestBody SignupDTO signupDTO) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.updateUser(currentEmail, signupDTO);
    }
}
