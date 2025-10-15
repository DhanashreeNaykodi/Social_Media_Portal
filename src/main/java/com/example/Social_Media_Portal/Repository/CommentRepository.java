package com.example.Social_Media_Portal.Repository;

import com.example.Social_Media_Portal.Entity.Comment;
import com.example.Social_Media_Portal.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByStatus (Enum e);

    @Query("SELECT c FROM Comment c WHERE c.user.userId = :userId")
    List<Comment> findByUserId(@Param("userId") Long userId);

//    List<Comment> findByUserId (Long userId);
}
