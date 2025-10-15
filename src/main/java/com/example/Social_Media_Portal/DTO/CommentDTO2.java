package com.example.Social_Media_Portal.DTO;

import com.example.Social_Media_Portal.Constants.Status;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO2 {

    private Long commentId;
    private String content;
    private LocalDateTime dateTime;
    private Status status;
    //new added
    private Long postId;
    private Long userId;

    //new added
    private String username;
}
