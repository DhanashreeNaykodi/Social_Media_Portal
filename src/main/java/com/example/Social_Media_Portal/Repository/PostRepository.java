package com.example.Social_Media_Portal.Repository;

import com.example.Social_Media_Portal.Entity.Post;
import com.example.Social_Media_Portal.Constants.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByStatus (Enum e);

    Page<Post> findByStatus(Status status, Pageable pageable); // Updated for pagination

//    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.commentList c WHERE p.status = :status AND (c IS EMPTY OR c.status = 'APPROVED')")
//    Page<Post> findByStatus(@Param("status") Status status, Pageable pageable);
}
