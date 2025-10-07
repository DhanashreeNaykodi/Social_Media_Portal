package com.example.Social_Media_Portal.Service;

import com.example.Social_Media_Portal.DTO.LoginDTO;
import com.example.Social_Media_Portal.DTO.SignupDTO;
import com.example.Social_Media_Portal.Entity.Role;
import com.example.Social_Media_Portal.Entity.User;
import com.example.Social_Media_Portal.Repository.PostRepository;
import com.example.Social_Media_Portal.Repository.UserRepository;
import com.example.Social_Media_Portal.Security.JWTAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTAuth jwtAuth;

    public ResponseEntity<String> saveNewUser(SignupDTO signupDTO) {
        if(userRepository.findByEmail(signupDTO.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists!");
        }
        User user = new User();
        user.setUser_name(signupDTO.getUsername());
        user.setEmail(signupDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));

        //normal user
        user.setModerator(false);
        user.setRole(Role.USER);
        userRepository.save(user);
        return ResponseEntity.ok("ID " + user.getUser_id() + " - User registered successfully.");
    }


    //      List<Post> entity ! use dto.
    public ResponseEntity<String> loginUser(LoginDTO loginDTO) {

        if(userRepository.findByEmail(loginDTO.getEmail()).isPresent()) {
            if(passwordEncoder.matches(loginDTO.getPassword(), userRepository.findByEmail(loginDTO.getEmail()).get().getPassword())) {
                User user = userRepository.findByEmail(loginDTO.getEmail()).get();
                String token = jwtAuth.generateToken(user);
                return ResponseEntity.ok("Successful Login. \nJWT Token : " + token);
            }
            throw new RuntimeException("Invalid Password");
        }
        throw new RuntimeException("No user registered with this email!");
    }

}
