package com.fluxbank.repository;

import com.fluxbank.entity.CBCurrency;
import com.fluxbank.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CBCurrencyRepository extends JpaRepository<CBCurrency, Integer> {
    Optional<CBCurrency> findByCode(Currency currency);
}
