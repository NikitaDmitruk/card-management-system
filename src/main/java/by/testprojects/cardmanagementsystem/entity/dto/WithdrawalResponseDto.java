package by.testprojects.cardmanagementsystem.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class WithdrawalResponseDto {
    private UUID id;
    private String cardMaskedNumber;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
    private BigDecimal cardBalance;
    private String status;
}
