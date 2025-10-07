package com.example.Social_Media_Portal.DTO;


import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupDTO {

    private String username;
    private String email;
    private String password;
}
