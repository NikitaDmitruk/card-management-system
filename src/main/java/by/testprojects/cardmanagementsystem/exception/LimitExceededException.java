package by.testprojects.cardmanagementsystem.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.math.BigDecimal;
import java.net.URI;

/**
 * Базовое исключение при превышении лимитов карты
 */
public class LimitExceededException extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("https://api.company.com/problems/limit-exceeded");

    public LimitExceededException(String detail) {
        super(TYPE, "Превышение лимита", Status.BAD_REQUEST, detail);
    }

    public LimitExceededException(String limitType, BigDecimal limit, BigDecimal attempted, BigDecimal used) {
        super(TYPE,
                "Превышение лимита",
                Status.BAD_REQUEST,
                String.format("Превышен %s. Лимит: %s, Попытка операции: %s, Уже использовано: %s",
                        limitType, limit, attempted, used));
    }
}