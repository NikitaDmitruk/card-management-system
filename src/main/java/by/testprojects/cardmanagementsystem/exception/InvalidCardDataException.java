package by.testprojects.cardmanagementsystem.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class InvalidCardDataException extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("https://api.bank.com/problems/invalid-card-data");

    public InvalidCardDataException(String detail) {
        super(TYPE, "Invalid card data", Status.BAD_REQUEST, detail);
    }
}