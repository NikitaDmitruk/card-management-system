package by.testprojects.cardmanagementsystem.service.validation;

import by.testprojects.cardmanagementsystem.entity.Card;
import by.testprojects.cardmanagementsystem.entity.CardStatus;
import by.testprojects.cardmanagementsystem.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для валидации операций с банковскими картами.
 * Выполняет проверки статуса карты, сроков действия, принадлежности и других параметров.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CardValidator {

    /**
     * Проверяет возможность выполнения операции с картой.
     *
     * @param card   карта для проверки
     * @param amount сумма операции
     * @return true если операция возможна
     * @throws CardOperationException если карта неактивна
     * @throws InvalidCardDataException если сумма некорректна или карта просрочена
     */
    public boolean cardIsValidForTransaction(Card card, BigDecimal amount) {
        validateCardStatus(card);
        validateAmount(amount);
        validateExpiryDate(card);

        log.debug("Валидация карты {} пройдена успешно", card.getId());
        return true;
    }

    /**
     * Проверяет, что карты разные.
     *
     * @param card1 первая карта
     * @param card2 вторая карта
     * @throws InvalidCardDataException если карты совпадают
     */
    public void validateNotSameCard(Card card1, Card card2) {
        if (card1.getId().equals(card2.getId())) {
            throw new InvalidCardDataException("Невозможно выполнить операцию между одинаковыми картами");
        }
    }

    /**
     * Проверяет, что карты принадлежат одному пользователю.
     *
     * @param card1 первая карта
     * @param card2 вторая карта
     * @throws InvalidCardDataException если карты принадлежат разным пользователям
     */
    public void validateSameOwner(Card card1, Card card2) {
        if (!card1.getUser().getId().equals(card2.getUser().getId())) {
            throw new InvalidCardDataException("Операции между картами разных пользователей запрещены");
        }
    }

    /**
     * Проверяет принадлежность карты пользователю.
     *
     * @param card карта для проверки
     * @param userCards карты пользователя
     * @throws CardNotFoundException если карта не принадлежит пользователю
     */
    public void validateCardOwner(Card card, List<Card> userCards) {
        if (!userCards.contains(card)) {
            throw new CardNotFoundException(card.getId());
        }
    }

    private void validateCardStatus(Card card) {
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new CardOperationException(
                    String.format("Карта не активна. Текущий статус: %s", card.getStatus())
            );
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidCardDataException("Сумма операции должна быть положительной");
        }
    }

    private void validateExpiryDate(Card card) {
        if (card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new InvalidCardDataException(
                    String.format("Срок действия карты истек (%s)", card.getExpiryDate())
            );
        }
    }
}