package com.example.Social_Media_Portal.DTO;

import com.example.Social_Media_Portal.Constants.Status;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO2 {

    private Long postId;

    private String content;
    private Status status;
    private LocalDateTime dateTime;
    private Long userId;
    private String username;

    //new added to show approved comments also with posts on feed
    private List<CommentDTO2> commentDTOList;
}