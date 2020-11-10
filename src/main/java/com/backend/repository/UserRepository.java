package com.backend.repository;

import com.backend.domain.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserResponse, Integer> {
    Optional<UserResponse> findByUsername(String username);
}
