package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Role;
import com.ecommerce.backend.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}
