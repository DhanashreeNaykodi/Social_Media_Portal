package com.example.Social_Media_Portal.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "Post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Post_ID")
    private Long post_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_Post")
    private User user;

    @Column(name = "Post_Content")
    @NotBlank(message = "Post Content cannot be empty")
    private String content;

    @Column(name = "createdAt")
    private LocalDateTime dateTime;

    @Column(name = "Status")
    @Enumerated(EnumType.STRING)
    private Status status;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> commentList;
}
