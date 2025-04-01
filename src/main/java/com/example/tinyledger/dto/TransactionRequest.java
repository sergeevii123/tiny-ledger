package com.example.tinyledger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record TransactionRequest(
    @Schema(description = "Amount of money for the transaction", example = "100.00", required = true)
    BigDecimal amount,
    
    @Schema(description = "Description of the transaction", example = "Monthly salary deposit", required = true)
    String description
) {} 