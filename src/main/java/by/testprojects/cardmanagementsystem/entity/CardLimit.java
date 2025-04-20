package by.testprojects.cardmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import static by.testprojects.cardmanagementsystem.Constants.*;


@Data
@Embeddable
public class CardLimit {

    @Column(name = "daily_withdrawal_limit", nullable = false)
    private BigDecimal dailyWithdrawalLimit = DEFAULT_CARD_DAILY_WITHDRAWAL_LIMIT;

    @Column(name = "monthly_withdrawal_limit", nullable = false)
    private BigDecimal monthlyWithdrawalLimit = DEFAULT_MONTHLY_WITHDRAWAL_LIMIT;

    @Column(name = "daily_transfer_limit", nullable = false)
    private BigDecimal dailyTransferLimit = DEFAULT_DAILY_TRANSFER_LIMIT;

    @Column(name = "monthly_transfer_limit", nullable = false)
    private BigDecimal monthlyTransferLimit = DEFAULT_MONTHLY_TRANSFER_LIMIT;

    @Column(name = "daily_withdrawal_used", nullable = false)
    private BigDecimal dailyWithdrawalUsed = BigDecimal.ZERO;

    @Column(name = "monthly_withdrawal_used", nullable = false)
    private BigDecimal monthlyWithdrawalUsed = BigDecimal.ZERO;

    @Column(name = "daily_transfer_used", nullable = false)
    private BigDecimal dailyTransferUsed = BigDecimal.ZERO;

    @Column(name = "monthly_transfer_used", nullable = false)
    private BigDecimal monthlyTransferUsed = BigDecimal.ZERO;

    @Column(name = "limit_reset_date", nullable = false)
    private LocalDate limitResetDate = LocalDate.now();
}