package tinyledger.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction(
    String id,
    String accountId,
    BigDecimal amount,
    TransactionType type,
    String description,
    LocalDateTime timestamp
) {
    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL
    }

    public Transaction(String id, String accountId, BigDecimal amount, TransactionType type, String description) {
        this(id, accountId, amount, type, description, LocalDateTime.now());
    }
} 