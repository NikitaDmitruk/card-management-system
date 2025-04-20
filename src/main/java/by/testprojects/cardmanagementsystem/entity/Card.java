package by.testprojects.cardmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bank_cards")
public class BankCard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "encrypted_number", nullable = false)
    private String encryptedNumber;  // AES-256 + HSM

    @Column(name = "masked_number", nullable = false)
    private String maskedNumber;     // Пример: "424242******4242"

    @Column(name = "card_holder", nullable = false)
    private String cardHolder;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;    // Формат MM/YY

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;      // ACTIVE, BLOCKED, EXPIRED

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;     // DECIMAL(19,4)

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

}