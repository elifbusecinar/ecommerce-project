package com.ecommerce.backend.service;

import com.ecommerce.backend.model.User;
import com.ecommerce.backend.model.Role;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalStateException("Username already exists: " + user.getUsername());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);

        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        
        if (!user.getUsername().equals(userDetails.getUsername())) {
            if (userRepository.existsByUsername(userDetails.getUsername())) {
                throw new IllegalStateException("Username already exists: " + userDetails.getUsername());
            }
            user.setUsername(userDetails.getUsername());
        }

        return userRepository.save(user);
    }

    public void updatePassword(Long id, String currentPassword, String newPassword) {
        User user = getUserById(id);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalStateException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long id) {
        User user = getUserById(id);
        user.setActive(true);
        userRepository.save(user);
    }

    public void addRoleToUser(Long userId, Role role) {
        User user = getUserById(userId);
        user.getRoles().add(role);
        userRepository.save(user);
    }

    public void removeRoleFromUser(Long userId, Role role) {
        User user = getUserById(userId);
        user.getRoles().remove(role);
        userRepository.save(user);
    }

    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRole(roleName);
    }

    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    public List<User> getInactiveUsers(LocalDateTime threshold) {
        return userRepository.findInactiveUsers(threshold);
    }

    public List<User> getUsersWithNoRoles() {
        return userRepository.findUsersWithNoRoles();
    }

    public List<User> getAllAdmins() {
        return userRepository.findAllAdmins();
    }

    public void updateLastLogin(Long id) {
        User user = getUserById(id);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
} 