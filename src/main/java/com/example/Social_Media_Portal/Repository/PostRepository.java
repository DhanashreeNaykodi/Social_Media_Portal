package com.example.Social_Media_Portal.Repository;

import com.example.Social_Media_Portal.Entity.Post;
import com.example.Social_Media_Portal.Constants.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatus (Status e);

    Page<Post> findByStatus(Status status, Pageable pageable); // Updated for pagination

}
