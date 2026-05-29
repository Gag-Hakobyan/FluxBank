package com.fluxbank.service;

import com.fluxbank.dto.TransactionResponseDto;
import com.fluxbank.dto.account.AccountResponseDto;
import com.fluxbank.dto.account.DepositRequestDto;
import com.fluxbank.dto.account.TransferRequestDto;
import com.fluxbank.entity.Account;
import com.fluxbank.entity.User;

import java.util.List;

public interface AccountService {
    Account save(Account account);

    void createDefaultAccount(User user);

    List<AccountResponseDto> getAllByUserEmail(String email);

    Account getByNumberAndUserEmail(String number, String email);

    TransactionResponseDto deposit(String number, DepositRequestDto depositRequestDto, String email);

    Account getByNumber(String number);

    TransactionResponseDto transfer(String number, String email, TransferRequestDto transferRequestDto);
}
