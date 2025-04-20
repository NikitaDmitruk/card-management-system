package by.testprojects.cardmanagementsystem.exception;

import java.math.BigDecimal;
import java.net.URI;

/**
 * Исключение при превышении месячного лимита снятия наличных
 */
public class MonthlyWithdrawalLimitExceededException extends LimitExceededException {
    private static final URI TYPE = URI.create("https://api.company.com/problems/monthly-withdrawal-limit-exceeded");

    public MonthlyWithdrawalLimitExceededException(BigDecimal limit, BigDecimal attempted, BigDecimal used) {
        super("месячный лимит снятия", limit, attempted, used);
    }
}
