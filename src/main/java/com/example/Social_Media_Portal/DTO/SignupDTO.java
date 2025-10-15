package com.example.Social_Media_Portal.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupDTO {

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9._-]*$", message = "Username must start with a letter and can only contain letters, numbers, dots, underscores, or hyphens")
    private String username;


    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9._-]*@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email must start with a letter, should have @, have valid domain and be valid like example@gmail.com")
    private String email;


    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be minimum of 8 length")
    @Pattern(
            regexp = ".*\\d.*",
            message = "Password must contain at least one number"
    )
    private String password;
}
