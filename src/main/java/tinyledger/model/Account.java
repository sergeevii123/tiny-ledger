package tinyledger.model;

import java.math.BigDecimal;

public record Account(
    String id,
    String name,
    BigDecimal balance
) {
    public Account withBalance(BigDecimal newBalance) {
        return new Account(id, name, newBalance);
    }
} 