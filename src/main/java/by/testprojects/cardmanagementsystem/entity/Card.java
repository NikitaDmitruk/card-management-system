package by.testprojects.cardmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cards")
public class Card {
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
    @Column(nullable = false)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status = CardStatus.ACTIVE;      // ACTIVE, BLOCKED, EXPIRED

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;     // DECIMAL(19,4)

    @Embedded
    CardLimit cardLimit = new CardLimit();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToStringExclude
    private User user; // Привязка к пользователю

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    @ToStringExclude
    private List<Transaction> transactions;

    @CreatedDate
    private LocalDateTime createdAt;
}