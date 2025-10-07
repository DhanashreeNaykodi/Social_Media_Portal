package com.example.Social_Media_Portal.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "User")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_ID")
    private Long user_id;

    @Column(name = "User_name")
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, or hyphens")
    private String user_name;


    // unique email id
    @Column(name = "Email", unique = true)
    @NotBlank(message = "Email cannot be empty")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Email(message = "Please provide a valid email address")
    private String email;

    @Column(name = "Password")
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be minimum of 8 length")
    private String password;

    @Column(name = "isModerator")
    private boolean isModerator;

    @Column(name = "Role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> postList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> commentList;
}
