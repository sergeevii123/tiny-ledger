package tinyledger.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateAccountRequest(
    @Schema(description = "Name of the account holder", example = "User name", required = true)
    String name
) {} 