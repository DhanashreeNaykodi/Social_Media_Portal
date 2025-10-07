package com.example.Social_Media_Portal.Repository;

import com.example.Social_Media_Portal.Entity.Post;
import com.example.Social_Media_Portal.Entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatus (Enum e);
}
