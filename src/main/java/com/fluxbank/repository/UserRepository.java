package com.fluxbank.repository;

import com.fluxbank.entity.User;
import com.fluxbank.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndId(String email, long id);

    Optional<User> findByVerifyToken(String token);

    List<User> findAllByEnabled(boolean enabled);

    Page<User> findAllByRole(UserRole role, Pageable pageable);

    Optional<User> findByIdAndRole(long id, UserRole role);
}
