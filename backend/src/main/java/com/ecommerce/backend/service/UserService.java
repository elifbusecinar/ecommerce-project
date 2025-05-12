package com.ecommerce.backend.service;

import com.ecommerce.backend.model.User;
import com.ecommerce.backend.model.Role;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.exception.ResourceNotFoundException;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.payload.dto.UserDTO; // UserDTO import edildi
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.backend.model.Address;
import com.ecommerce.backend.model.RoleName;
import com.ecommerce.backend.repository.RoleRepository;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors; // Collectors import edildi

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    // ... (createUser, updateUser, vb. diğer metodlar burada)

    /**
     * Depodaki tüm kullanıcıları DTO olarak ID'ye göre sıralı getirir.
     * Admin kullanımı için tasarlanmıştır.
     * @return Tüm UserDTO'ların bir listesi.
     */
    public List<UserDTO> getAllUsersAsDTO() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return users.stream()
                    .map(UserDTO::fromEntity) // User entity'sini UserDTO'ya çevir
                    .collect(Collectors.toList());
    }

    // Eğer User entity'lerini döndüren eski getAllUsers metoduna hala ihtiyaç varsa:
    public List<User> getAllUserEntities() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
    
    // ... (diğer metodların devamı)
    // Önceki yanıtlardaki UserService içeriğini buraya kopyalayabilirsiniz,
    // sadece getAllUsers metodunu yukarıdaki gibi getAllUsersAsDTO ile değiştirin
    // veya getAllUsers metodunun dönüş tipini ve içeriğini List<UserDTO> yapın.
    // Aşağıda örnek olarak diğer metodları da bırakıyorum, ancak bu metodlar DTO değil entity döndürüyor.
    // İhtiyaca göre bu metodların da DTO döndürmesi gerekebilir.

    public User createUser(User user) { // Bu metod User entity döndürüyor
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalStateException("Username already exists: " + user.getUsername());
        }
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already exists: " + user.getEmail());
        }
        if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
            for (Address address : user.getAddresses()) {
                address.setUser(user);
            }
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Error: Default role ROLE_USER is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) { // Bu metod User entity döndürüyor
        User user = getUserById(id);
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        if (userDetails.getUsername() != null && !user.getUsername().equals(userDetails.getUsername())) {
            if (userRepository.existsByUsername(userDetails.getUsername())) {
                throw new IllegalStateException("Username already exists: " + userDetails.getUsername());
            }
            user.setUsername(userDetails.getUsername());
        }
        if (userDetails.getAddresses() != null) {
            user.getAddresses().clear();
            for (Address addressDetail : userDetails.getAddresses()) {
                addressDetail.setUser(user);
                user.getAddresses().add(addressDetail);
            }
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

    public User getUserById(Long id) { // Bu metod User entity döndürüyor
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public Optional<User> getUserByUsername(String username) { // Bu metod User entity döndürüyor
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> getUserByEmail(String email) { // Bu metod User entity döndürüyor
        return userRepository.findByEmail(email);
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
        Role managedRole = roleRepository.findByName(role.getName())
            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + role.getName()));
        user.getRoles().add(managedRole);
        userRepository.save(user);
    }

    public void removeRoleFromUser(Long userId, Role role) {
        User user = getUserById(userId);
        Role managedRole = roleRepository.findByName(role.getName())
            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + role.getName()));
        user.getRoles().remove(managedRole);
        userRepository.save(user);
    }
    public List<User> getUsersByRole(String roleNameString) { // Bu metod User entity listesi döndürüyor
        RoleName roleName;
        try {
            roleName = RoleName.valueOf(roleNameString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role name: " + roleNameString);
        }
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found in database: " + roleNameString));
        return userRepository.findByRole(role.getName().name());
    }
    public List<User> getActiveUsers() { // Bu metod User entity listesi döndürüyor
        return userRepository.findByActiveTrue();
    }
    public List<User> getInactiveUsers(LocalDateTime threshold) { // Bu metod User entity listesi döndürüyor
        return userRepository.findInactiveUsers(threshold);
    }
    public List<User> getUsersWithNoRoles() { // Bu metod User entity listesi döndürüyor
        return userRepository.findUsersWithNoRoles();
    }
    public List<User> getAllAdmins() { // Bu metod User entity listesi döndürüyor
        return userRepository.findAllAdmins();
    }
    public void updateLastLogin(Long id) {
        User user = getUserById(id);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
}
