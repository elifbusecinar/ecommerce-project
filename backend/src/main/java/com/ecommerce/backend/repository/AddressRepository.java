package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
    
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.isDefault = true")
    Optional<Address> findDefaultAddress(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(a) FROM Address a WHERE a.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    void deleteByUserIdAndId(Long userId, Long addressId);
} 