package by.testprojects.cardmanagementsystem.entity.dto;

import by.testprojects.cardmanagementsystem.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private UUID id;
    private UUID cardId;
    private BigDecimal amount;
    private String description;
    private TransactionType type;
    private LocalDateTime timestamp;
}