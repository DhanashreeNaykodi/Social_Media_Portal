package com.example.Social_Media_Portal.Controller;


import com.example.Social_Media_Portal.DTO.CommentDTO;
import com.example.Social_Media_Portal.DTO.PostDTO;
import com.example.Social_Media_Portal.Service.ModeratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moderator")
public class ModeratorController {

    @Autowired
    ModeratorService moderatorService;

    @GetMapping("posts/pending")
//    @PreAuthorize("hasRole('MODERATOR')")
    public List<PostDTO> getPendingPost () {
        return moderatorService.PendingRequests();
    }

    @GetMapping("posts/approved")
    public List<PostDTO> getApprovedPost () {
        return moderatorService.ApprovedRequests();
    }

    @GetMapping("posts/rejected")
    public List<PostDTO> getRejectedPost () {
        return moderatorService.RejectedRequests();
    }

    @PatchMapping("posts/approve/{id}")
    public String approvePost (@PathVariable Long id) {
        return moderatorService.approvePost(id);
    }

    @PatchMapping("posts/reject/{id}")
    public String rejectPost (@PathVariable Long id) {
        return moderatorService.rejectPost(id);
    }



    @GetMapping("comments/pending")
    public List<CommentDTO> getPendingComment () {
        return moderatorService.PendingComments();
    }

    @GetMapping("comments/approved")
    public List<CommentDTO> getApprovedComment () {
        return moderatorService.ApprovedComments();
    }

    @GetMapping("comments/rejected")
    public List<CommentDTO> getRejectedComment () {
        return moderatorService.RejectedComments();
    }

    @PatchMapping("comments/approve/{id}")
    public String approveComment (@PathVariable Long id) {
        return moderatorService.approveComments(id);
    }

    @PatchMapping("comments/reject/{id}")
    public String rejectComment (@PathVariable Long id) {
        return moderatorService.rejectComments(id);
    }

}
