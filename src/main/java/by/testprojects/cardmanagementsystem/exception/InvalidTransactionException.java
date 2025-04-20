package by.testprojects.cardmanagementsystem.exception;

import org.zalando.problem.Status;

// Неверные параметры транзакции
public class InvalidTransactionException extends TransactionException {
    public InvalidTransactionException(String detail) {
        super(Status.BAD_REQUEST, "Invalid Transaction", detail);
    }
}
