package com.example.Social_Media_Portal.DTO;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CommentUpdateDTO {

    @NotBlank(message = "Content cannot be empty")
    private String content;

    private Long postId;
}
