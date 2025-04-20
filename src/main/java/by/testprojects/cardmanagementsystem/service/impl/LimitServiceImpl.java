package by.testprojects.cardmanagementsystem.service.impl;

import by.testprojects.cardmanagementsystem.entity.Card;
import by.testprojects.cardmanagementsystem.entity.CardLimit;
import by.testprojects.cardmanagementsystem.exception.*;
import by.testprojects.cardmanagementsystem.repository.CardRepository;
import by.testprojects.cardmanagementsystem.service.LimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Реализация сервиса для управления лимитами карт.
 * Обеспечивает проверку лимитов при операциях, автоматический сброс лимитов по расписанию
 * и ведение учета использованных лимитов.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LimitServiceImpl implements LimitService {

    private final CardRepository cardRepository;

    /**
     * Проверяет, не превысит ли указанная сумма снятия дневной лимит карты.
     *
     * @param card   карта для проверки лимитов
     * @param amount запрашиваемая сумма снятия
     * @throws DailyWithdrawalLimitExceededException если дневной лимит снятия будет превышен
     * @throws LimitNotResetException                если лимиты не были сброшены для текущего периода
     */
    @Override
    @Transactional
    public void checkDailyWithdrawalLimit(Card card, BigDecimal amount) {
        validateLimitResetDate(card);

        CardLimit limit = card.getCardLimit();
        if (limit.getDailyWithdrawalLimit() != null) {
            BigDecimal newUsage = limit.getDailyWithdrawalUsed().add(amount);
            if (newUsage.compareTo(limit.getDailyWithdrawalLimit()) > 0) {
                log.warn("Попытка превышения дневного лимита снятия наличных для карты {}.", card.getId());

                throw new DailyWithdrawalLimitExceededException(
                        limit.getDailyWithdrawalLimit(),
                        amount,
                        limit.getDailyWithdrawalUsed()
                );
            }
        }
    }

    /**
     * Проверяет, не превысит ли указанная сумма снятия месячный лимит карты.
     *
     * @param card   карта для проверки лимитов
     * @param amount запрашиваемая сумма снятия
     * @throws MonthlyWithdrawalLimitExceededException если месячный лимит снятия будет превышен
     * @throws LimitNotResetException                  если лимиты не были сброшены для текущего периода
     */
    @Override
    @Transactional
    public void checkMonthlyWithdrawalLimit(Card card, BigDecimal amount) {
        validateLimitResetDate(card);

        CardLimit limit = card.getCardLimit();
        if (limit.getMonthlyWithdrawalLimit() != null) {
            BigDecimal newUsage = limit.getMonthlyWithdrawalUsed().add(amount);
            if (newUsage.compareTo(limit.getMonthlyWithdrawalLimit()) > 0) {
                log.warn("Попытка превышения месячного лимита снятия наличных для карты {}.", card.getId());
                throw new MonthlyWithdrawalLimitExceededException(
                        limit.getMonthlyWithdrawalLimit(),
                        amount,
                        limit.getMonthlyWithdrawalUsed()
                );
            }
        }
    }

    /**
     * Проверяет, не превысит ли указанная сумма перевода дневной лимит карты.
     *
     * @param card   карта для проверки лимитов
     * @param amount запрашиваемая сумма перевода
     * @throws DailyTransferLimitExceededException если дневной лимит переводов будет превышен
     * @throws LimitNotResetException              если лимиты не были сброшены для текущего периода
     */
    @Override
    @Transactional
    public void checkDailyTransferLimit(Card card, BigDecimal amount) {
        validateLimitResetDate(card);

        CardLimit limit = card.getCardLimit();
        if (limit.getDailyTransferLimit() != null) {
            BigDecimal newUsage = limit.getDailyTransferUsed().add(amount);
            if (newUsage.compareTo(limit.getDailyTransferLimit()) > 0) {
                log.warn("Попытка превышения дневного лимита перевода для карты {}.", card.getId());
                throw new DailyTransferLimitExceededException(
                        limit.getDailyTransferLimit(),
                        amount,
                        limit.getDailyTransferUsed()
                );
            }
        }
    }

    /**
     * Проверяет, не превысит ли указанная сумма перевода месячный лимит карты.
     *
     * @param card   карта для проверки лимитов
     * @param amount запрашиваемая сумма перевода
     * @throws MonthlyTransferLimitExceededException если месячный лимит переводов будет превышен
     * @throws LimitNotResetException                если лимиты не были сброшены для текущего периода
     */
    @Override
    @Transactional
    public void checkMonthlyTransferLimit(Card card, BigDecimal amount) {
        validateLimitResetDate(card);

        CardLimit limit = card.getCardLimit();
        if (limit.getMonthlyTransferLimit() != null) {
            BigDecimal newUsage = limit.getMonthlyTransferUsed().add(amount);
            if (newUsage.compareTo(limit.getMonthlyTransferLimit()) > 0) {
                log.warn("Попытка превышения месячного лимита перевода для карты {}.", card.getId());
                throw new MonthlyTransferLimitExceededException(
                        limit.getMonthlyTransferLimit(),
                        amount,
                        limit.getMonthlyTransferUsed()
                );
            }
        }
    }

    /**
     * Увеличивает счетчик использованных лимитов для операции снятия наличных.
     *
     * @param card   карта для обновления лимитов
     * @param amount сумма снятия для учета в лимитах
     * @throws LimitNotResetException если лимиты не были сброшены для текущего периода
     */
    @Override
    @Transactional
    public void addWithdrawalUsage(Card card, BigDecimal amount) {
        validateLimitResetDate(card);

        CardLimit limit = card.getCardLimit();
        limit.setDailyWithdrawalUsed(limit.getDailyWithdrawalUsed().add(amount));
        limit.setMonthlyWithdrawalUsed(limit.getMonthlyWithdrawalUsed().add(amount));
        cardRepository.save(card);
    }

    /**
     * Увеличивает счетчик использованных лимитов для операции перевода средств.
     *
     * @param card   карта для обновления лимитов
     * @param amount сумма перевода для учета в лимитах
     * @throws LimitNotResetException если лимиты не были сброшены для текущего периода
     */
    @Override
    @Transactional
    public void addTransferUsage(Card card, BigDecimal amount) {
        validateLimitResetDate(card);

        CardLimit limit = card.getCardLimit();
        limit.setDailyTransferUsed(limit.getDailyTransferUsed().add(amount));
        limit.setMonthlyTransferUsed(limit.getMonthlyTransferUsed().add(amount));
        cardRepository.save(card);
    }

    /**
     * Автоматический сброс дневных лимитов всех карт. Выполняется ежедневно в 00:00.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetDailyLimits() {
        LocalDate today = LocalDate.now();
        List<Card> cards = cardRepository.findAll();

        cards.forEach(card -> {
            CardLimit limit = card.getCardLimit();
            limit.setDailyWithdrawalUsed(BigDecimal.ZERO);
            limit.setDailyTransferUsed(BigDecimal.ZERO);
            limit.setLimitResetDate(today);
        });

        cardRepository.saveAll(cards);
        log.info("Дневные лимиты сброшены для {} карт", cards.size());
    }

    /**
     * Автоматический сброс месячных лимитов всех карт. Выполняется 1 числа каждого месяца в 00:10.
     */
    @Scheduled(cron = "0 10 0 1 * *")
    @Transactional
    public void resetMonthlyLimits() {
        List<Card> cards = cardRepository.findAll();

        cards.forEach(card -> {
            CardLimit limit = card.getCardLimit();
            limit.setMonthlyWithdrawalUsed(BigDecimal.ZERO);
            limit.setMonthlyTransferUsed(BigDecimal.ZERO);
        });

        cardRepository.saveAll(cards);
        log.info("Месячные лимиты сброшены для {} карт", cards.size());
    }

    /**
     * Проверяет, были ли сброшены лимиты для текущего периода.
     *
     * @param card карта для проверки
     * @throws LimitNotResetException если лимиты не были сброшены для текущего дня/месяца
     */
    private void validateLimitResetDate(Card card) {
        LocalDate today = LocalDate.now();
        CardLimit limit = card.getCardLimit();

        if (limit.getLimitResetDate() == null ||
                !limit.getLimitResetDate().isEqual(today)) {
            throw new LimitNotResetException(
                    limit.getLimitResetDate(),
                    today
            );
        }
    }
}