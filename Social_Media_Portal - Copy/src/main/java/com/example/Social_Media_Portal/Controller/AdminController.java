package com.example.Social_Media_Portal.Controller;


import com.example.Social_Media_Portal.DTO.SignupDTO;
import com.example.Social_Media_Portal.DTO.UsersDTO;
import com.example.Social_Media_Portal.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @GetMapping("/users")
    public List<UsersDTO> getOnlyUsers () {
        return adminService.getUsers();
    }

    @GetMapping("/moderators")
    public List<UsersDTO> getOnlyModerators () {
        return adminService.getModerators();
    }

    @PatchMapping("/upgrade/{id}")
    public ResponseEntity<String> upgradeUser (@PathVariable Long id) {
        return adminService.upgradeUser(id);
    }

    @PostMapping("/createMod")
    public ResponseEntity<String> createNewModerator (@RequestBody SignupDTO signupDTO) {
        return adminService.newModerator(signupDTO);
    }

    @PatchMapping("/downgrade/{id}")
    public ResponseEntity<String> removeMod (@PathVariable Long id) {
        return adminService.downgradeModerator(id);
    }
}
