package com.fluxbank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CBCurrencyResponseDto {
    @JsonProperty("USD")
    private BigDecimal usd;

    @JsonProperty("RUB")
    private BigDecimal rub;

    @JsonProperty("EUR")
    private BigDecimal eur;
}
