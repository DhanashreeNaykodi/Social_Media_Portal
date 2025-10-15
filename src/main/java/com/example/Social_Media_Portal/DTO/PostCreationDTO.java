package com.example.Social_Media_Portal.DTO;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreationDTO {

    @NotBlank(message = "Content cannot be empty")
    private String content;
}
