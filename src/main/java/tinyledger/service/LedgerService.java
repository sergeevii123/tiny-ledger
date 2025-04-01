package tinyledger.service;

import tinyledger.model.Account;
import tinyledger.model.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class LedgerService {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, List<Transaction>> accountTransactions = new ConcurrentHashMap<>();

    public Account createAccount(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Account name cannot be empty");
        }
        String id = UUID.randomUUID().toString();
        Account account = new Account(id, name, BigDecimal.ZERO);
        accounts.put(id, account);
        accountTransactions.put(id, new CopyOnWriteArrayList<>());
        return account;
    }

    public Account getAccount(String accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        return account;
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public synchronized List<Transaction> transferMoney(String fromAccountId, String toAccountId, 
                                                      BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Account fromAccount = getAccount(fromAccountId);
        Account toAccount = getAccount(toAccountId);
        if (fromAccount.id().equals(toAccount.id())) {
            throw new IllegalArgumentException("Source and destination accounts cannot be the same");
        }
        if (fromAccount.balance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in source account");
        }

        // Create withdrawal transaction
        Transaction withdrawalTx = recordTransaction(
            fromAccountId, 
            amount, 
            Transaction.TransactionType.WITHDRAWAL, 
            "Transfer to " + toAccount.name() + ": " + description
        );

        // Create deposit transaction
        Transaction depositTx = recordTransaction(
            toAccountId, 
            amount, 
            Transaction.TransactionType.DEPOSIT, 
            "Transfer from " + fromAccount.name() + ": " + description
        );

        return List.of(withdrawalTx, depositTx);
    }

    public Transaction recordTransaction(String accountId, BigDecimal amount, 
                                      Transaction.TransactionType type, String description) {
        Account account = getAccount(accountId);
        BigDecimal newBalance;

        if (type == Transaction.TransactionType.WITHDRAWAL && account.balance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in account: " + accountId);
        }

        newBalance = switch (type) {
            case DEPOSIT -> account.balance().add(amount);
            case WITHDRAWAL -> account.balance().subtract(amount);
        };

        String transactionId = UUID.randomUUID().toString();
        Transaction transaction = new Transaction(transactionId, accountId, amount, type, description);
        
        accounts.put(accountId, account.withBalance(newBalance));
        accountTransactions.get(accountId).add(transaction);
        
        return transaction;
    }

    public BigDecimal getBalance(String accountId) {
        return getAccount(accountId).balance();
    }

    public List<Transaction> getTransactionHistory(String accountId) {
        if (!accountTransactions.containsKey(accountId)) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        return new ArrayList<>(accountTransactions.get(accountId));
    }
} 