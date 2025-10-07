package com.example.Social_Media_Portal.DTO;


import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreationDTO {
    private String content;
    private Long user_id;
}
