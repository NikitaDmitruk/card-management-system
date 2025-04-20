package by.testprojects.cardmanagementsystem.service;

import by.testprojects.cardmanagementsystem.entity.Card;
import by.testprojects.cardmanagementsystem.entity.CardType;
import by.testprojects.cardmanagementsystem.entity.dto.CreateCardRequest;
import by.testprojects.cardmanagementsystem.entity.mapper.CardMapper;
import by.testprojects.cardmanagementsystem.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardGeneratorService {

    private static final Random random = new Random();
    private final CardEncryptionService encryptionService;
    private final CardMapper cardMapper;

    public Card generateCard(CreateCardRequest createCardRequest, User user) {
        Card card = cardMapper.toCard(createCardRequest);
        card.setUser(user);
        card.setCardHolder(createCardRequest.getCardHolder());
        card.setCardType(createCardRequest.getCardType());
        card.setExpiryDate(generateExpiryDate(createCardRequest.getDurationYears()));
        card.setBalance(createCardRequest.getInitialBalance());
        String cardNumber = generateCardNumber(createCardRequest.getCardType());
        card.setMaskedNumber(encryptionService.mask(cardNumber));
        card.setEncryptedNumber(encryptionService.encrypt(cardNumber));
        return card;
    }

    private String generateCardNumber(CardType cardType) {
        String prefix = switch (cardType) {
            case VISA -> "4";
            case MASTERCARD -> "5";
            case MIR -> "2";
            default -> "3"; // American Express и другие
        };

        // Генерация 15 цифр для VISA/MC (16 цифр всего)
        String middle = random.ints(15, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());

        String number = prefix + middle;
        return number + calculateLuhnCheckDigit(number);
    }

//    public String generateCVV() {
//        return String.format("%03d", random.nextInt(1000));
//    }

    private LocalDate generateExpiryDate(Integer durationYears) {
        return LocalDate.now()
                .plusYears(durationYears)
                .with(TemporalAdjusters.lastDayOfMonth())
                .plusDays(1);
    }

    private int calculateLuhnCheckDigit(String number) {
        // Реализация алгоритма Луна
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) digit = (digit % 10) + 1;
            }
            sum += digit;
            alternate = !alternate;
        }
        return (10 - (sum % 10)) % 10;
    }
}