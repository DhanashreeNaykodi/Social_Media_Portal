package com.example.Social_Media_Portal.DTO;
import com.example.Social_Media_Portal.Entity.Role;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UsersDTO {

    private Long user_id;
    private String user_name;
    private String email;
    private boolean isModerator;
    private Role role;
//    private List<Post> postList;
}
