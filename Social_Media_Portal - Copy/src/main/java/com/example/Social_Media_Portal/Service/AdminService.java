package com.example.Social_Media_Portal.Service;


import com.example.Social_Media_Portal.DTO.SignupDTO;
import com.example.Social_Media_Portal.DTO.UsersDTO;
import com.example.Social_Media_Portal.Entity.Role;
import com.example.Social_Media_Portal.Entity.User;
import com.example.Social_Media_Portal.Repository.UserRepository;
import com.example.Social_Media_Portal.Security.JWTAuth;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTAuth jwtAuth;


    public List<UsersDTO> getUsers() {
        if(userRepository.findAll().isEmpty()) {
            return Collections.emptyList();
        }
        return userRepository.findByRole(Role.USER).stream().map(user -> new UsersDTO(
                user.getUser_id(),
                user.getUser_name(),
                user.getEmail(),
                user.isModerator(),
                user.getRole()
        )).collect(Collectors.toList());
    }

    public List<UsersDTO> getModerators() {
        if(userRepository.findAll().isEmpty()) {
            return Collections.emptyList();
        }
        return userRepository.findByRole(Role.MODERATOR).stream().map(user -> new UsersDTO(
                user.getUser_id(),
                user.getUser_name(),
                user.getEmail(),
                user.isModerator(),
                user.getRole()
        )).collect(Collectors.toList());
    }

    //The @Transactional annotation ensures the method runs within a database transaction, which is required for persisting changes.
    //Without this, the save operation might not commit to the database, depending on your transaction configuration.
    //@Transactional - working fine without this also..

    public ResponseEntity<String> upgradeUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("NO such user present."));
        if(user.isModerator()) {
            return ResponseEntity.ok("Given ID is already a Moderator.");
        }
        user.setModerator(true);
        user.setRole(Role.MODERATOR);
        userRepository.save(user);
        String token = jwtAuth.generateToken(user);
        return ResponseEntity.ok("User upgraded to Moderator successfully!\nNew token : " + token);
    }

    public ResponseEntity<String> newModerator(SignupDTO signupDTO) {
        if(userRepository.findByEmail(signupDTO.getEmail()).isPresent()) {
            return ResponseEntity.ok("User with given email already present in system.");
        }
        User user = new User();
        user.setUser_name(signupDTO.getUsername());
        user.setEmail(signupDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));

        user.setModerator(true);
        user.setRole(Role.MODERATOR);
        userRepository.save(user);
//        String token = jwtAuth.generateToken(user);

        return ResponseEntity.ok("ID " + user.getUser_id() + " - Moderator registered successfully");
    }

    public ResponseEntity<String> downgradeModerator(Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("NO such user present."));
        if(!user.isModerator()) {
            return ResponseEntity.ok("Given ID is not a moderator.");
        }
        user.setModerator(false);
        user.setRole(Role.USER);
        userRepository.save(user);
        String token = jwtAuth.generateToken(user);
        return ResponseEntity.ok("Moderator downgraded to User successfully!\nNew token : " + token);
    }
}
