package com.example.Social_Media_Portal.Repository;

import com.example.Social_Media_Portal.Constants.Role;
import com.example.Social_Media_Portal.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail (String email);

    List<User> findByRole (Role e);
}
