package com.comtialsign.bancodigital.repositories;

import com.comtialsign.bancodigital.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório do model do usuário {@link User}
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
