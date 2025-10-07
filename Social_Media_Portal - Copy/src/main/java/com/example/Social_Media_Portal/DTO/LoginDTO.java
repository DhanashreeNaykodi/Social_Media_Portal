package com.example.Social_Media_Portal.DTO;


import lombok.*;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginDTO {
    private String email;
    private String password;
}
