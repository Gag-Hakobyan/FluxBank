package com.fluxbank.dto.account;

import com.fluxbank.enums.AccountStatus;
import com.fluxbank.enums.AccountType;
import com.fluxbank.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDto {
    private String number;
    private String name;
    private BigDecimal balance;
    private Currency currency;
    private AccountStatus status;
    private AccountType type;
}
