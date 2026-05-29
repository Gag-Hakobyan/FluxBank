package com.fluxbank.dto;

import com.fluxbank.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponseDto {
    private long id;
    private BigDecimal amount;
    private TransactionType type;
    private String senderName;
    private String senderNumber;
    private String recipientName;
    private String recipientNumber;
    private LocalDateTime createdDate;
}
