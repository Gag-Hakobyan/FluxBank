package com.fluxbank.dto.account;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequestDto {
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "100", message = "Minimum deposit is 100.00")
    @DecimalMax(value = "1000000", message = "Maximum deposit is 1000000.00")
    @Digits(integer = 8, fraction = 0)
    private BigDecimal amount;

    @NotBlank(message = "Recipient number is required")
    @Pattern(
            regexp = "^\\d{20}$",
            message = "Account number must contain exactly 20 digits"
    )
    private String recipientNumber;
}
