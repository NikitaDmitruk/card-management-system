package by.testprojects.cardmanagementsystem.service;

import by.testprojects.cardmanagementsystem.entity.Card;

import java.math.BigDecimal;

public interface LimitService {

    void checkDailyWithdrawalLimit(Card card, BigDecimal amount);

    void checkMonthlyWithdrawalLimit(Card card, BigDecimal amount);

    void checkDailyTransferLimit(Card card, BigDecimal amount);

    void checkMonthlyTransferLimit(Card card, BigDecimal amount);

    void resetDailyLimits();

    void resetMonthlyLimits();

    void addWithdrawalUsage(Card card, BigDecimal amount);

    void addTransferUsage(Card card, BigDecimal amount);
}
