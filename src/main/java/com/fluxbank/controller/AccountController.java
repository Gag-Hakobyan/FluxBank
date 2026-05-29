package com.fluxbank.controller;

import com.fluxbank.dto.TransactionResponseDto;
import com.fluxbank.dto.account.AccountResponseDto;
import com.fluxbank.dto.account.DepositRequestDto;
import com.fluxbank.dto.account.TransferRequestDto;
import com.fluxbank.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
@Validated
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getAll(Authentication authentication) {
        return ResponseEntity.ok(accountService.getAllByUserEmail(authentication.getName()));
    }

    @PostMapping("/{number}/deposit")
    public ResponseEntity<TransactionResponseDto> deposit(
            @PathVariable
            @Pattern(
                    regexp = "^\\d{20}$",
                    message = "Account number must contain exactly 20 digits"
            )
            String number,
            @RequestBody @Valid DepositRequestDto depositRequestDto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(accountService.deposit(number, depositRequestDto, authentication.getName()));
    }

    @PostMapping("/{number}/transfer")
    public ResponseEntity<TransactionResponseDto> transfer(
            @PathVariable
            @Pattern(
                    regexp = "^\\d{20}$",
                    message = "Account number must contain exactly 20 digits"
            )
            String number,
            @RequestBody @Valid TransferRequestDto transferRequestDto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                accountService.transfer(number, authentication.getName(), transferRequestDto));
    }
}