package com.studentportal.portal.repository;

import com.studentportal.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Data JPA automatically writes the SQL to find a user by their email
    Optional<User> findByEmail(String email);
    
    // We can add more custom methods here later if needed, e.g. finding by role
}
