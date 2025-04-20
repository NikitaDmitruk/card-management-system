package by.testprojects.cardmanagementsystem.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.UUID;

// Транзакция не найдена
public class TransactionNotFoundException extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("https://api.company.com/problems/transaction-not-found");
    public TransactionNotFoundException(UUID id) {
        super(TYPE, "Transaction Not Found", Status.NOT_FOUND,
                String.format("Transaction with ID '%s' does not exist", id));
    }
}