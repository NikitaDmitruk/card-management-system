package by.testprojects.cardmanagementsystem.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransferResponseDto {
    private UUID transferId;
    private String fromCardMaskedNumber;
    private String toCardMaskedNumber;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
    private BigDecimal fromCardNewBalance;
    private BigDecimal toCardNewBalance;
    private String status;
}