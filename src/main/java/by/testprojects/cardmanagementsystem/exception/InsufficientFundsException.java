package by.testprojects.cardmanagementsystem.exception;

import org.zalando.problem.Status;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientFundsException extends TransactionException {
    public InsufficientFundsException(UUID cardId, BigDecimal amount) {
        super(Status.BAD_REQUEST,
                "Недостаточно средств",
                String.format("Недостаточно средств для снятия. Доступно: %s.", amount));
    }
}