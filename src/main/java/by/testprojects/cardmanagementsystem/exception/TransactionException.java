package by.testprojects.cardmanagementsystem.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

// Ошибка транзакции (базовый класс)
public class TransactionException extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("https://api.company.com/problems/transaction-error");
    protected TransactionException(Status status, String title, String detail) {
        super(TYPE, title, status, detail);
    }
}