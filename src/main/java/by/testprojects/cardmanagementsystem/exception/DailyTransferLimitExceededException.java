package by.testprojects.cardmanagementsystem.exception;

import java.math.BigDecimal;
import java.net.URI;

/**
 * Исключение при превышении дневного лимита переводов
 */
public class DailyTransferLimitExceededException extends LimitExceededException {
    private static final URI TYPE = URI.create("https://api.company.com/problems/daily-transfer-limit-exceeded");

    public DailyTransferLimitExceededException(BigDecimal limit, BigDecimal attempted, BigDecimal used) {
        super("дневной лимит переводов", limit, attempted, used);
    }
}
