package by.testprojects.cardmanagementsystem.entity.dto;

import by.testprojects.cardmanagementsystem.entity.CardStatus;
import by.testprojects.cardmanagementsystem.entity.CardType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.UUID;

import static by.testprojects.cardmanagementsystem.Constants.EXPIRY_DATE_FORMAT_FOR_CLIENT;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private UUID id;
    private String maskedNumber;
    private String cardHolder;
    private CardType cardType;
    @JsonFormat(pattern = EXPIRY_DATE_FORMAT_FOR_CLIENT)
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal balance;
}
