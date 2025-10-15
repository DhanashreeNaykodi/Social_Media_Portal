package com.example.Social_Media_Portal.DTO;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreationDTO {

    @NotBlank(message = "Content cannot be blank")
    private String content;

    private Long postId;
}
