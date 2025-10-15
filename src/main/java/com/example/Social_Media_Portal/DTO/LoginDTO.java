package com.example.Social_Media_Portal.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginDTO {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9._-]*@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Email must start with a letter and be valid like example@gmail.com"
    )
    private String email;


    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be minimum of 8 length")
    private String password;
}
