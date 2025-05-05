package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRole(@Param("roleName") String roleName);

    List<User> findByActiveTrue();

    @Query("SELECT u FROM User u WHERE u.lastLogin < :threshold")
    List<User> findInactiveUsers(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT u FROM User u WHERE SIZE(u.roles) = 0")
    List<User> findUsersWithNoRoles();

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_ADMIN'")
    List<User> findAllAdmins();
}
