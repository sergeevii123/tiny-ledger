package com.example.tinyledger.controller;

import com.example.tinyledger.dto.CreateAccountRequest;
import com.example.tinyledger.model.Account;
import com.example.tinyledger.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "Account Management", description = "APIs for managing bank accounts")
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final LedgerService ledgerService;

    AccountController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @Operation(summary = "Create a new account", description = "Creates a new bank account with the given name")
    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(ledgerService.createAccount(request.name()));
    }

    @Operation(summary = "Get all accounts", description = "Returns a list of all bank accounts with their current balances")
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(ledgerService.getAllAccounts());
    }

    @Operation(summary = "Get account details", description = "Returns details of a specific account including its current balance")
    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountId) {
        try {
            return ResponseEntity.ok(ledgerService.getAccount(accountId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

} 