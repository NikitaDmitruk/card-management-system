package by.testprojects.cardmanagementsystem.exception;

import java.math.BigDecimal;
import java.net.URI;

/**
 * Исключение при превышении дневного лимита снятия наличных
 */
public class DailyWithdrawalLimitExceededException extends LimitExceededException {
    private static final URI TYPE = URI.create("https://api.company.com/problems/daily-withdrawal-limit-exceeded");

    public DailyWithdrawalLimitExceededException(BigDecimal limit, BigDecimal attempted, BigDecimal used) {
        super("дневной лимит снятия", limit, attempted, used);
    }
}
