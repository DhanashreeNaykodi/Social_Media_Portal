package com.example.Social_Media_Portal.DTO;

import com.example.Social_Media_Portal.Constants.Status;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long postId;
    private String content;
    private Status status;
    private LocalDateTime dateTime;
    private Long userId;

    //new field
    private String username;

    //new added to show approved comments also with posts on feed
//    private List<CommentDTO> commentDTOList;
}

