package by.testprojects.cardmanagementsystem.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

// Недопустимая операция с картой
public class CardOperationException extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("https://api.company.com/problems/card-operation-error");
    public CardOperationException(String detail) {
        super(TYPE, "Card Operation Failed", Status.BAD_REQUEST, detail);
    }
}