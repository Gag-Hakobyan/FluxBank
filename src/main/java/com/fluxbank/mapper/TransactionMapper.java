package com.fluxbank.mapper;

import com.fluxbank.dto.TransactionResponseDto;
import com.fluxbank.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    Transaction toEntity(TransactionResponseDto transactionResponseDto);


    @Mapping(target = "senderName",
            source = "senderAccount.user.name")

    @Mapping(target = "senderNumber",
            source = "senderAccount.number")

    @Mapping(target = "recipientName",
            source = "recipientAccount.user.name")

    @Mapping(target = "recipientNumber",
            source = "recipientAccount.number")
    TransactionResponseDto toDto(Transaction transaction);
}
