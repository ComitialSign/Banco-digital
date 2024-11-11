package com.comtialsign.bancodigital.repositories;

import com.comtialsign.bancodigital.domain.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório do model de Transaction {@link Transaction}
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
