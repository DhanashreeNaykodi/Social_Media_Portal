package com.example.Social_Media_Portal.DTO;

import com.example.Social_Media_Portal.Entity.Status;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    private Long comment_id;
    private String content;
    private LocalDateTime dateTime;
    private Status status;
    private Long post_id;

    //new added
//    private Long user_id;

}
