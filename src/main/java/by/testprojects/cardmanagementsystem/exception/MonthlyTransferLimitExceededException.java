package by.testprojects.cardmanagementsystem.exception;

import java.math.BigDecimal;
import java.net.URI;

/**
 * Исключение при превышении месячного лимита переводов
 */
public class MonthlyTransferLimitExceededException extends LimitExceededException {
    private static final URI TYPE = URI.create("https://api.company.com/problems/monthly-transfer-limit-exceeded");

    public MonthlyTransferLimitExceededException(BigDecimal limit, BigDecimal attempted, BigDecimal used) {
        super("месячный лимит переводов", limit, attempted, used);
    }
}
