package tinyledger.controller;

import tinyledger.dto.CreateAccountRequest;
import tinyledger.dto.ErrorResponse;
import tinyledger.model.Account;
import tinyledger.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        try {
            if (request.name() == null || request.name().isBlank()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Account name cannot be empty", HttpStatus.BAD_REQUEST.value()));
            }
            return ResponseEntity.ok(ledgerService.createAccount(request.name()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Operation(summary = "Get all accounts", description = "Returns a list of all bank accounts with their current balances")
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(ledgerService.getAllAccounts());
    }

    @Operation(summary = "Get account details", description = "Returns details of a specific account including its current balance")
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable String accountId) {
        try {
            return ResponseEntity.ok(ledgerService.getAccount(accountId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        }
    }
} 