package com.foodgram.repository;

import com.foodgram.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndRole(String email, User.Role role);

    boolean existsByEmail(String email);

    // New methods for admin APIs
    Page<User> findByRole(User.Role role, Pageable pageable);

    Page<User> findByStatus(User.Status status, Pageable pageable);

    Page<User> findByRoleAndStatus(User.Role role, User.Status status, Pageable pageable);

    List<User> findAllByRole(User.Role role);
}