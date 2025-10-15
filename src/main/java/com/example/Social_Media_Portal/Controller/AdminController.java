package com.example.Social_Media_Portal.Controller;


import com.example.Social_Media_Portal.DTO.SignupDTO;
import com.example.Social_Media_Portal.DTO.UsersDTO;
import com.example.Social_Media_Portal.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    UserService userService;
    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UsersDTO>> getUsers () {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/moderators")
    public ResponseEntity<List<UsersDTO>> getModerators () {
        return ResponseEntity.ok(userService.getModerators());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/upgrade/{userId}")
    public ResponseEntity<String> upgradeUser (@PathVariable Long userId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.upgradeUser(userId, currentEmail);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createModerator")
    public ResponseEntity<String> createNewModerator (@RequestBody SignupDTO signupDTO) {
        return userService.newModerator(signupDTO);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/downgrade/{moderatorId}")
    public ResponseEntity<String> removeMod (@PathVariable Long moderatorId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.downgradeModerator(moderatorId, currentEmail);

    }
}
