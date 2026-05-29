package com.fluxbank.mapper;

import com.fluxbank.dto.account.AccountResponseDto;
import com.fluxbank.entity.Account;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountResponseDto toDto(Account account);

    List<AccountResponseDto> toDtoList(List<Account> accounts);

    Account toEntity(AccountResponseDto accountResponseDto);

    List<Account> toEntityList(List<AccountResponseDto> accountResponseDtos);
}
