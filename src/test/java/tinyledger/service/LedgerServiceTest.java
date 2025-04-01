package tinyledger.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tinyledger.model.Account;
import tinyledger.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LedgerServiceTest {
    private LedgerService ledgerService;

    @BeforeEach
    void setUp() {
        ledgerService = new LedgerService();
    }

    @Test
    void createAccount_WithValidName_ShouldCreateAccount() {
        // Given
        String accountName = "User Name";

        // When
        Account account = ledgerService.createAccount(accountName);

        // Then
        assertNotNull(account.id());
        assertEquals("User Name", account.name());
        assertEquals(BigDecimal.ZERO, account.balance());
    }

    @Test
    void createAccount_WithEmptyName_ShouldThrowException() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> ledgerService.createAccount(""));
    }

    @Test
    void getAccount_WithValidId_ShouldReturnAccount() {
        // Given
        Account createdAccount = ledgerService.createAccount("User Name");

        // When
        Account retrievedAccount = ledgerService.getAccount(createdAccount.id());

        // Then
        assertNotNull(retrievedAccount);
        assertEquals(createdAccount.id(), retrievedAccount.id());
        assertEquals(createdAccount.name(), retrievedAccount.name());
        assertEquals(createdAccount.balance(), retrievedAccount.balance());
    }

    @Test
    void getAccount_WithInvalidId_ShouldThrowException() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> ledgerService.getAccount("invalid-id"));
    }

    @Test
    void recordTransaction_WithDeposit_ShouldUpdateBalance() {
        // Given
        Account account = ledgerService.createAccount("User Name");
        BigDecimal amount = new BigDecimal("100.00");

        // When
        Transaction transaction = ledgerService.recordTransaction(
            account.id(), amount, Transaction.TransactionType.DEPOSIT, "Initial deposit");

        // Then
        Account updatedAccount = ledgerService.getAccount(account.id());
        assertEquals(amount, updatedAccount.balance());
        assertEquals(account.id(), transaction.accountId());
        assertEquals(Transaction.TransactionType.DEPOSIT, transaction.type());
    }

    @Test
    void recordTransaction_WithWithdrawal_ShouldUpdateBalance() {
        // Given
        Account account = ledgerService.createAccount("User Name");
        BigDecimal depositAmount = new BigDecimal("100.00");
        BigDecimal withdrawalAmount = new BigDecimal("50.00");

        // When
        ledgerService.recordTransaction(account.id(), depositAmount, Transaction.TransactionType.DEPOSIT, "Initial deposit");
        Transaction withdrawal = ledgerService.recordTransaction(
            account.id(), withdrawalAmount, Transaction.TransactionType.WITHDRAWAL, "Withdrawal");

        // Then
        Account updatedAccount = ledgerService.getAccount(account.id());
        assertEquals(new BigDecimal("50.00"), updatedAccount.balance());
        assertEquals(account.id(), withdrawal.accountId());
        assertEquals(Transaction.TransactionType.WITHDRAWAL, withdrawal.type());
    }

    @Test
    void recordTransaction_WithInsufficientFunds_ShouldThrowException() {
        // Given
        Account account = ledgerService.createAccount("User Name");
        BigDecimal amount = new BigDecimal("100.00");

        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
            ledgerService.recordTransaction(account.id(), amount, Transaction.TransactionType.WITHDRAWAL, "Withdrawal"));
    }

    @Test
    void transferMoney_WithValidAmount_ShouldUpdateBalances() {
        // Given
        Account fromAccount = ledgerService.createAccount("From User");
        Account toAccount = ledgerService.createAccount("To User");
        BigDecimal initialDeposit = new BigDecimal("100.00");
        BigDecimal transferAmount = new BigDecimal("50.00");

        // When
        ledgerService.recordTransaction(fromAccount.id(), initialDeposit, Transaction.TransactionType.DEPOSIT, "Initial deposit");
        List<Transaction> transactions = ledgerService.transferMoney(
            fromAccount.id(), toAccount.id(), transferAmount, "Transfer");

        // Then
        Account updatedFromAccount = ledgerService.getAccount(fromAccount.id());
        Account updatedToAccount = ledgerService.getAccount(toAccount.id());
        assertEquals(new BigDecimal("50.00"), updatedFromAccount.balance());
        assertEquals(transferAmount, updatedToAccount.balance());
        assertEquals(2, transactions.size());
    }

    @Test
    void transferMoney_WithInsufficientFunds_ShouldThrowException() {
        // Given
        Account fromAccount = ledgerService.createAccount("From User");
        Account toAccount = ledgerService.createAccount("To User");
        BigDecimal transferAmount = new BigDecimal("100.00");

        // When/Then
        assertThrows(IllegalArgumentException.class, () ->
            ledgerService.transferMoney(fromAccount.id(), toAccount.id(), transferAmount, "Transfer"));
    }

    @Test
    void getTransactionHistory_ShouldReturnAllTransactions() {
        // Given
        Account account = ledgerService.createAccount("User Name");
        BigDecimal amount = new BigDecimal("100.00");

        // When
        ledgerService.recordTransaction(account.id(), amount, Transaction.TransactionType.DEPOSIT, "Initial deposit");
        List<Transaction> history = ledgerService.getTransactionHistory(account.id());

        // Then
        assertEquals(1, history.size());
        assertEquals(account.id(), history.get(0).accountId());
        assertEquals(amount, history.get(0).amount());
        assertEquals(Transaction.TransactionType.DEPOSIT, history.get(0).type());
    }

    @Test
    void getTransactionHistory_WithInvalidAccountId_ShouldThrowException() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> 
            ledgerService.getTransactionHistory("invalid-id"));
    }
} 