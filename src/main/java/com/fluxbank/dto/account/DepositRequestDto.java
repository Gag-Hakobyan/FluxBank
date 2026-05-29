package com.fluxbank.dto.account;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositRequestDto {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "100", message = "Minimum deposit is 100.00")
    @DecimalMax(value = "1000000", message = "Maximum deposit is 1000000.00")
    @Digits(integer = 8, fraction = 0)
    private BigDecimal amount;
}
