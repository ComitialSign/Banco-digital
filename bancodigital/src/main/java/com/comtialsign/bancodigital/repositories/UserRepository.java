package com.comtialsign.bancodigital.repositories;

import com.comtialsign.bancodigital.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByDocument(String username);

    Optional<User> findUserById(Long id);
}
