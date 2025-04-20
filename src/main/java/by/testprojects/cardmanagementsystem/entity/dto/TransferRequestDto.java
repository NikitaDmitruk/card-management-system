package by.testprojects.cardmanagementsystem.entity.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferRequestDto {
    @NotNull(message = "Source card ID is required")
    private UUID fromCardId;

    @NotNull(message = "Target card ID is required")
    private UUID toCardId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String description;
}
