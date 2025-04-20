package by.testprojects.cardmanagementsystem.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.time.LocalDate;

/**
 * Исключение при использовании карты без сброса лимитов для текущего периода
 */
public class LimitNotResetException extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("https://api.company.com/problems/limit-not-reset");

    public LimitNotResetException(LocalDate lastResetDate, LocalDate currentDate) {
        super(TYPE,
                "Лимиты не сброшены",
                Status.BAD_REQUEST,
                String.format("Лимиты не были сброшены для текущего периода. Дата последнего сброса: %s, Текущая дата: %s",
                        lastResetDate, currentDate));
    }
}