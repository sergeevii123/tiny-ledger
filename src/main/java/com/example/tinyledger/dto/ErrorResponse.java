package com.example.tinyledger.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
    @Schema(description = "Error message describing what went wrong", example = "Insufficient funds for withdrawal")
    String message,
    
    @Schema(description = "HTTP status code", example = "400")
    int status
) {} 