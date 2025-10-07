package com.example.Social_Media_Portal.Controller;

import com.example.Social_Media_Portal.DTO.LoginDTO;
import com.example.Social_Media_Portal.DTO.SignupDTO;
import com.example.Social_Media_Portal.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> RegisterUser(@RequestBody SignupDTO signupDTO) {
        return authService.saveNewUser(signupDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDTO loginDTO) {
        return authService.loginUser(loginDTO);
    }
}



