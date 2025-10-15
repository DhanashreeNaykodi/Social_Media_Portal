package com.example.Social_Media_Portal.Service;
import com.example.Social_Media_Portal.DTO.*;
import com.example.Social_Media_Portal.Constants.Role;
import com.example.Social_Media_Portal.Entity.User;
import com.example.Social_Media_Portal.Exception.EmailAlreadyExistsException;
import com.example.Social_Media_Portal.Exception.InvalidPasswordException;
import com.example.Social_Media_Portal.Exception.UnauthorizedActionException;
import com.example.Social_Media_Portal.Exception.UserNotFoundException;
import com.example.Social_Media_Portal.Repository.UserRepository;
import com.example.Social_Media_Portal.Security.JWTAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTAuth jwtAuth;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTAuth jwtAuth) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuth = jwtAuth;
    }


    //    from auth
    public ResponseEntity<Map<String, Object>> saveNewUser(SignupDTO signupDTO) {
        if(userRepository.findByEmail(signupDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("User already exists!");
        }
        User user = new User();
        user.setUsername(signupDTO.getUsername());
        user.setEmail(signupDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));

        //normal user
        user.setRole(Role.USER);
        userRepository.save(user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("userId", user.getUserId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    public ResponseEntity<Map<String, Object>> loginUser(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(()
                -> new UserNotFoundException("User not found for given email"));

        if(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {

            String token = jwtAuth.generateToken(user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User Logged In successfully");
            response.put("userId", user.getUserId());
            response.put("email", user.getEmail());
            response.put("Token", token);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        throw new InvalidPasswordException("Invalid Password");
    }



    public ResponseEntity<UsersDTO> getUserProfile(String currentEmail) {
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));

        return ResponseEntity.ok(new UsersDTO(
                currentUser.getUserId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getRole()
        ));
    }

    public void deleteUser(String currentEmail) {
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));

        userRepository.delete(currentUser);
    }



    public ResponseEntity<UsersDTO> updateUser(String currentEmail, SignupDTO signupDTO) {
        User currentUser = userRepository.findByEmail(currentEmail).orElseThrow(()
                -> new UserNotFoundException("Authenticated user not found"));

        currentUser.setEmail(signupDTO.getEmail());
        currentUser.setUsername(signupDTO.getUsername());
        currentUser.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        userRepository.save(currentUser);

        return ResponseEntity.ok(new UsersDTO(
                currentUser.getUserId(),
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getRole()
        ));
    }


    // for moderator
//    public void deleteUserById(String currentEmail, Long id) {
//        User currentUser = userRepository.findByEmail(currentEmail)
//                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));
//        User deletingUserId = userRepository.findById(id)
//                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));
//
//        if(!(currentUser.getRole() == Role.MODERATOR))  {
//            throw new UnauthorizedActionException("You are not authorized to perform this operation");
//        }
//        userRepository.delete(deletingUserId);
//    }


    //from adminService
    public List<UsersDTO> getUsers() {
        if(userRepository.findAll().isEmpty()) {
            return Collections.emptyList();
        }
        return userRepository.findByRole(Role.USER).stream().map(user -> new UsersDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        )).collect(Collectors.toList());
    }

    public List<UsersDTO> getModerators() {
        if(userRepository.findAll().isEmpty()) {
            return Collections.emptyList();
        }
        return userRepository.findByRole(Role.MODERATOR).stream().map(user -> new UsersDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        )).collect(Collectors.toList());
    }


    public ResponseEntity<String> upgradeUser(Long userId, String currentEmail) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("No such user present."));

        User currentUser = userRepository.findByEmail(currentEmail).orElseThrow(()
                -> new UserNotFoundException("No authenticated user present"));

        if(currentUser.getEmail().equals(user.getEmail())) {
            throw new UnauthorizedActionException("You(Admin) cannot be a moderator.");
        }

        user.setRole(Role.MODERATOR);
        userRepository.save(user);
        return ResponseEntity.ok("User upgraded to Moderator successfully!\nLogin again to access Moderator privileges");
    }



    public ResponseEntity<String> newModerator(SignupDTO signupDTO) {
        if(userRepository.findByEmail(signupDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("User with given email already present in system.");
        }
        User user = new User();
        user.setUsername(signupDTO.getUsername());
        user.setEmail(signupDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));

        user.setRole(Role.MODERATOR);
        userRepository.save(user);
        return ResponseEntity.ok("ID " + user.getUserId() + " - Moderator registered successfully");
    }


    public ResponseEntity<String> downgradeModerator(Long moderatorId, String currentEmail) {
        User user = userRepository.findById(moderatorId).orElseThrow(()
                -> new UserNotFoundException("No moderator present with given ID."));

        User currentUser = userRepository.findByEmail(currentEmail).orElseThrow(()
                -> new UserNotFoundException("No authenticated user present"));

        if(currentUser.getEmail().equals(user.getEmail())) {
            throw new UnauthorizedActionException("You(Admin) cannot be an user");
        }

        user.setRole(Role.USER);
        userRepository.save(user);
        return ResponseEntity.ok("Moderator downgraded to User successfully!\nLogin again to access only User privileges");
    }


    //protected helper methods
    protected User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()
                -> new UserNotFoundException("Authenticated user not found by given email : " + email));
    }
}
