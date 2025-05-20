package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Cart;
import com.ecommerce.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Verilen kullanıcıya ait aktif sepeti bulur.
     * @param user Kullanıcı
     * @return Opsiyonel olarak kullanıcıya ait aktif sepet.
     */
    Optional<Cart> findByUserAndActiveTrue(User user);

    /**
     * Verilen misafir sepet ID'sine ait aktif sepeti bulur.
     * @param guestCartId Misafir sepet ID'si
     * @return Opsiyonel olarak misafir sepet ID'sine ait aktif sepet.
     */
    Optional<Cart> findByGuestCartIdAndActiveTrue(String guestCartId);
}