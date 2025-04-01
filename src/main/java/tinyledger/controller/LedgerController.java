package tinyledger.controller;

import tinyledger.dto.ErrorResponse;
import tinyledger.dto.TransactionRequest;
import tinyledger.dto.TransferRequest;
import tinyledger.model.Transaction;
import tinyledger.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "Transactions", description = "APIs for managing account transactions")
@RestController
@RequestMapping("/api/transactions")
public class LedgerController {
    private final LedgerService ledgerService;

    LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @Operation(summary = "Transfer money between accounts", 
              description = "Transfers money from one account to another")
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        try {
            List<Transaction> transactions = ledgerService.transferMoney(
                request.fromAccountId(), 
                request.toAccountId(), 
                request.amount(), 
                request.description());
            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Operation(summary = "Make a deposit", description = "Deposits money into an account")
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<?> deposit(
            @PathVariable String accountId,
            @RequestBody TransactionRequest request
    ) {
        try {
            Transaction transaction = ledgerService.recordTransaction(
                accountId, request.amount(), Transaction.TransactionType.DEPOSIT, request.description());
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Operation(summary = "Make a withdrawal", description = "Withdraws money from an account")
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable String accountId,
            @RequestBody TransactionRequest request
    ) {
        try {
            Transaction transaction = ledgerService.recordTransaction(
                accountId, request.amount(), Transaction.TransactionType.WITHDRAWAL, request.description());
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Operation(summary = "Get transaction history", description = "Returns all transactions for a specific account")
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getTransactions(@PathVariable String accountId) {
        try {
            return ResponseEntity.ok(ledgerService.getTransactionHistory(accountId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        }
    }
} 