package com.fluxbank.service.impl;

import com.fluxbank.dto.TransactionResponseDto;
import com.fluxbank.dto.account.AccountResponseDto;
import com.fluxbank.dto.account.DepositRequestDto;
import com.fluxbank.dto.account.TransferRequestDto;
import com.fluxbank.entity.Account;
import com.fluxbank.entity.Transaction;
import com.fluxbank.entity.User;
import com.fluxbank.enums.AccountStatus;
import com.fluxbank.enums.TransactionType;
import com.fluxbank.exception.NotFoundException;
import com.fluxbank.mapper.AccountMapper;
import com.fluxbank.mapper.TransactionMapper;
import com.fluxbank.repository.AccountRepository;
import com.fluxbank.service.AccountService;
import com.fluxbank.service.TransactionService;
import com.fluxbank.util.GenerateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final GenerateUtil generateUtil;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public void createDefaultAccount(User user) {
        String generatedNumber;

        do {
            generatedNumber = generateUtil.generate(20);
        } while (accountRepository.existsByNumber(generatedNumber));

        save(Account.builder()
                .number(generatedNumber)
                .user(user)
                .build());
    }

    @Override
    public List<AccountResponseDto> getAllByUserEmail(String email) {
        return accountMapper.toDtoList(accountRepository.findAllByUserEmail(email));
    }

    @Override
    public Account getByNumberAndUserEmail(String number, String email) {
        return accountRepository.findByNumberAndUserEmail(number, email).orElseThrow(() ->
                new NotFoundException("Account not found")
        );
    }

    @Override
    public Account getByNumber(String number) {
        return accountRepository.findByNumber(number)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    @Override
    @Transactional
    public TransactionResponseDto deposit(String number, DepositRequestDto depositRequestDto, String email) {
        Account account = getByNumberAndUserEmail(number, email);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }

        account.setBalance(account.getBalance().add(depositRequestDto.getAmount()));
        save(account);

        return transactionMapper.toDto(
                transactionService.save(Transaction.builder()
                        .amount(depositRequestDto.getAmount())
                        .type(TransactionType.DEPOSIT)
                        .senderAccount(account)
                        .recipientAccount(account)
                        .build())
        );
    }

    @Override
    @Transactional
    public TransactionResponseDto transfer(String number, String email, TransferRequestDto transferRequestDto) {
        Account senderAccount = getByNumberAndUserEmail(number, email);

        if (senderAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Sender account is not active");
        }

        if (number.equals(transferRequestDto.getRecipientNumber())) {
            throw new IllegalArgumentException("Cannot transfer money to the same account");
        }

        Account recipientAccount = getByNumber(transferRequestDto.getRecipientNumber());

        if (recipientAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Recipient account is not active");
        }

        if (senderAccount.getBalance().compareTo(transferRequestDto.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(transferRequestDto.getAmount()));
        save(senderAccount);

        recipientAccount.setBalance(recipientAccount.getBalance().add(transferRequestDto.getAmount()));
        save(recipientAccount);

        return transactionMapper.toDto(
                transactionService.save(Transaction.builder()
                        .amount(transferRequestDto.getAmount())
                        .type(TransactionType.TRANSFER)
                        .senderAccount(senderAccount)
                        .recipientAccount(recipientAccount)
                        .build()));
    }
}
