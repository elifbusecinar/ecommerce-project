package com.ecommerce.backend;

import com.ecommerce.backend.model.Role;
import com.ecommerce.backend.model.RoleName;
import com.ecommerce.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.HashSet;


@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // ROLE_USER varsa yoksa oluştur
        if (roleRepository.findByName(RoleName.ROLE_USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(RoleName.ROLE_USER);
            roleRepository.save(userRole);
        }

        // ROLE_ADMIN varsa yoksa oluştur
        if (roleRepository.findByName(RoleName.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }

        // eger admin kullanicisi yoksa olustur
        if (userRepository.findByUsername("admin@example.com").isEmpty()) {
            User adminUser = User.builder()
                    .username("admin@example.com")
                    .firstName("Admin")
                    .lastName("User")
                    .password(passwordEncoder.encode("admin123"))  // Şifreyi encode ediyoruz
                    .build();

            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role not found."));
            roles.add(adminRole);

            adminUser.setRoles(roles);
            userRepository.save(adminUser);
        }
    }
}
