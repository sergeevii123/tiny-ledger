package com.example.tinyledger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record TransferRequest(
    @Schema(description = "Source account ID", example = "00000000-0000-0000-0000-000000000000", required = true)
    String fromAccountId,
    
    @Schema(description = "Destination account ID", example = "00000000-0000-0000-0000-000000000001", required = true)
    String toAccountId,
    
    @Schema(description = "Amount of money to transfer", example = "50.00", required = true)
    BigDecimal amount,
    
    @Schema(description = "Description of the transfer", example = "Rent payment", required = true)
    String description
) {} 