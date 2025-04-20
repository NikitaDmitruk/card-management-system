package by.testprojects.cardmanagementsystem.service.impl;

import by.testprojects.cardmanagementsystem.entity.*;
import by.testprojects.cardmanagementsystem.entity.dto.*;
import by.testprojects.cardmanagementsystem.entity.mapper.TransactionMapper;
import by.testprojects.cardmanagementsystem.exception.*;
import by.testprojects.cardmanagementsystem.repository.CardRepository;
import by.testprojects.cardmanagementsystem.repository.TransactionRepository;
import by.testprojects.cardmanagementsystem.security.service.JwtService;
import by.testprojects.cardmanagementsystem.service.CardService;
import by.testprojects.cardmanagementsystem.service.LimitService;
import by.testprojects.cardmanagementsystem.service.TransactionService;
import by.testprojects.cardmanagementsystem.service.validation.CardValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static by.testprojects.cardmanagementsystem.Constants.TRANSACTION_STATUS_COMPLETED;

/**
 * Реализация сервиса для выполнения операций с транзакциями.
 * Обеспечивает выполнение переводов, пополнений и снятий средств с карт,
 * включая проверку лимитов и валидацию операций.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final TransactionMapper transactionMapper;
    private final LimitService limitService;
    private final CardValidator cardValidator;
    private final CardService cardService;

    /**
     * Выполняет перевод средств между картами одного пользователя.
     *
     * @param transferRequest DTO с данными для перевода
     * @return DTO с информацией о выполненном переводе
     * @throws CardNotFoundException      если одна из карт не найдена
     * @throws InvalidCardDataException   если карты невалидны для операции
     * @throws InsufficientFundsException если недостаточно средств на карте отправителя
     * @throws LimitExceededException     если превышены лимиты по переводам
     * @throws CardOperationException     если операция перевода невозможна
     */
    @Override
    @Transactional
    public TransferResponseDto transferBetweenSameOwner(TransferRequestDto transferRequest, User user) {
        Card fromCard = getCardWithValidation(transferRequest.getFromCardId(), user);
        Card toCard = getCardWithValidation(transferRequest.getToCardId(), user);

        validateTransferOperationBetweenSameOwner(fromCard, toCard, transferRequest.getAmount());
        checkTransferLimits(fromCard, transferRequest.getAmount());
        limitService.addTransferUsage(fromCard, transferRequest.getAmount());

        return processTransfer(fromCard, toCard, transferRequest);
    }

    /**
     * Выполняет пополнение карты.
     *
     * @param cardId идентификатор карты
     * @param amount сумма пополнения
     * @return DTO с информацией о транзакции
     * @throws CardNotFoundException    если карта не найдена
     * @throws InvalidCardDataException если карта невалидна для операции
     */

    @Override
    @Transactional
    public TransactionDto deposit(UUID cardId, BigDecimal amount, User user) {
        Card card = getCardWithValidation(cardId, user);
        cardValidator.cardIsValidForTransaction(card, amount);

        card.setBalance(card.getBalance().add(amount));
        Transaction transaction = createTransaction(
                card,
                amount,
                TransactionType.DEPOSIT,
                "Пополнение карты"
        );

        saveTransaction(card, transaction);
        log.info("Пополнение карты {} на сумму {}", card.getMaskedNumber(), amount);

        return transactionMapper.toDto(transaction);
    }

    /**
     * Выполняет снятие средств с карты.
     *
     * @param request запрос с данными о снятии суммы
     * @return DTO с информацией о транзакции
     * @throws CardNotFoundException      если карта не найдена
     * @throws InvalidCardDataException   если карта невалидна для операции
     * @throws InsufficientFundsException если недостаточно средств
     * @throws LimitExceededException     если превышены лимиты по снятию
     */
    @Override
    @Transactional
    public WithdrawalResponseDto withdraw(WithdrawalRequestDto request, User user) {
        Card card = getCardWithValidation(request.getCardId(), user);
        cardValidator.cardIsValidForTransaction(card, request.getAmount());

        validateWithdrawalAmount(card, request.getAmount());
        checkWithdrawalLimits(card, request.getAmount());
        limitService.addWithdrawalUsage(card, request.getAmount());

        card.setBalance(card.getBalance().subtract(request.getAmount()));
        Transaction transaction = createTransaction(
                card,
                request.getAmount().negate(),
                TransactionType.WITHDRAWAL,
                "Снятие наличных c карты " + card.getMaskedNumber()
        );

        saveTransaction(card, transaction);
        log.info("Снятие с карты {} суммы {}", card.getMaskedNumber(), request.getAmount());

        return transactionMapper.toWithdrawalResponse(transaction, TRANSACTION_STATUS_COMPLETED);
    }

    @Override
    public Page<TransactionDto> getFilteredTransactions(User principal,
                                                        UUID cardId,
                                                        LocalDate from,
                                                        LocalDate to,
                                                        Pageable pageable) {
        Long userId = principal.getId();
        Set<Role> roles = principal.getRoles();
        boolean isAdmin = roles.contains(Role.ADMIN);

        Specification<Transaction> spec = Specification.where(null);

        if (!isAdmin) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("card").get("user").get("id"), userId));
        }

        if (cardId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("card").get("id"), cardId));
        }

        if (from != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("timestamp"), from.atStartOfDay()));
        }

        if (to != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("timestamp"), to.atTime(23, 59, 59)));
        }

        return transactionRepository.findAll(spec, pageable)
                .map(transactionMapper::toDto);
    }


    private Card getCardWithValidation(UUID cardId, User user) {
        Card card = cardService.getCardById(cardId);
        List<Card> userCards = cardService.getAllCardsByUserId(user.getId());
        cardValidator.validateCardOwner(card, userCards);
        return card;
    }

    private void validateTransferOperationBetweenSameOwner(Card fromCard, Card toCard, BigDecimal amount) {
        cardValidator.cardIsValidForTransaction(fromCard, amount);
        cardValidator.cardIsValidForTransaction(toCard, amount);
        cardValidator.validateNotSameCard(fromCard, toCard);
        cardValidator.validateSameOwner(fromCard, toCard);


        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(fromCard.getId(), amount);
        }
    }

    private void checkTransferLimits(Card card, BigDecimal amount) {
        limitService.checkDailyTransferLimit(card, amount);
        limitService.checkMonthlyTransferLimit(card, amount);
    }

    private void checkWithdrawalLimits(Card card, BigDecimal amount) {
        limitService.checkDailyWithdrawalLimit(card, amount);
        limitService.checkMonthlyWithdrawalLimit(card, amount);
    }

    private void validateWithdrawalAmount(Card card, BigDecimal amount) {
        if (card.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(card.getId(), amount);
        }
    }

    private TransferResponseDto processTransfer(Card fromCard, Card toCard, TransferRequestDto request) {
        // Выполнение перевода
        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        // Создание транзакций
        Transaction withdrawal = createTransaction(
                fromCard,
                request.getAmount().negate(),
                TransactionType.TRANSFER_OUT,
                "Перевод на карту " + toCard.getMaskedNumber()
        );

        Transaction deposit = createTransaction(
                toCard,
                request.getAmount(),
                TransactionType.TRANSFER_IN,
                "Перевод с карты " + fromCard.getMaskedNumber()
        );

        // Сохранение изменений
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        transactionRepository.save(withdrawal);
        transactionRepository.save(deposit);

        log.info("Перевод с карты {} на карту {} суммы {}",
                fromCard.getMaskedNumber(),
                toCard.getMaskedNumber(),
                request.getAmount()
        );

        return transactionMapper.toTransferResponse(
                withdrawal,
                deposit,
                fromCard,
                toCard,
                TRANSACTION_STATUS_COMPLETED
        );
    }

    private Transaction createTransaction(Card card, BigDecimal amount, TransactionType type, String description) {
        return Transaction.builder()
                .card(card)
                .amount(amount)
                .type(type)
                .description(description)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private void saveTransaction(Card card, Transaction transaction) {
        cardRepository.save(card);
        transactionRepository.save(transaction);
    }
}