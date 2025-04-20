package by.testprojects.cardmanagementsystem.service;

import by.testprojects.cardmanagementsystem.entity.Card;

import java.math.BigDecimal;

public interface LimitUsageService {

    void addWithdrawalUsage(Card card, BigDecimal amount);

    void addTransferUsage(Card card, BigDecimal amount);
}
