package com.example.Social_Media_Portal.DTO;


import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreationDTO {
    private String content;
    private Long post_id;
    private Long userId;
}
