package com.fluxbank.repository;

import com.fluxbank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByNumber(String number);

    List<Account> findAllByUserEmail(String email);

    Optional<Account> findByNumberAndUserEmail(String number, String userEmail);

    Optional<Account> findByNumber(String number);
}
