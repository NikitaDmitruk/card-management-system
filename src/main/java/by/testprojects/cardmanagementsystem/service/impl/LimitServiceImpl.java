package by.testprojects.cardmanagementsystem.service.impl;

import by.testprojects.cardmanagementsystem.entity.Card;
import by.testprojects.cardmanagementsystem.entity.CardLimit;
import by.testprojects.cardmanagementsystem.repository.CardRepository;
import by.testprojects.cardmanagementsystem.service.LimitUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LimitUsageServiceImpl implements LimitUsageService {

    private final CardRepository cardRepository;

    @Override
    public void addWithdrawalUsage(Card card, BigDecimal amount) {
        resetLimitsIfNeeded(card);
        CardLimit limit = card.getCardLimit();

        limit.setDailyWithdrawalUsed(limit.getDailyWithdrawalUsed().add(amount));
        limit.setMonthlyWithdrawalUsed(limit.getMonthlyWithdrawalUsed().add(amount));

        cardRepository.save(card);
    }

    @Override
    public void addTransferUsage(Card card, BigDecimal amount) {
        resetLimitsIfNeeded(card);
        CardLimit limit = card.getCardLimit();

        limit.setDailyTransferUsed(limit.getDailyTransferUsed().add(amount));
        limit.setMonthlyTransferUsed(limit.getMonthlyTransferUsed().add(amount));

        cardRepository.save(card);
    }

    private void resetLimitsIfNeeded(Card card) {
        CardLimit limit = card.getCardLimit();
        LocalDate today = LocalDate.now();

        // Сброс дневных лимитов
        if (!limit.getLimitResetDate().isEqual(today)) {
            limit.setDailyTransferUsed(BigDecimal.ZERO);
            limit.setDailyWithdrawalUsed(BigDecimal.ZERO);
        }

        // Сброс месячных лимитов (если месяц сменился)
        if (limit.getLimitResetDate().getMonth() != today.getMonth()
                || limit.getLimitResetDate().getYear() != today.getYear()) {
            limit.setMonthlyTransferUsed(BigDecimal.ZERO);
            limit.setMonthlyWithdrawalUsed(BigDecimal.ZERO);
        }

        // Обновляем дату последнего сброса
        limit.setLimitResetDate(today);
    }
}
