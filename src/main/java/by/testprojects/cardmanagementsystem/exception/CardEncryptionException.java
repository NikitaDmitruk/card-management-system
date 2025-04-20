package by.testprojects.cardmanagementsystem.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class CardEncryptionException extends AbstractThrowableProblem {
    public CardEncryptionException(String message, Throwable cause) {
        super(null, "Encryption exception", Status.BAD_REQUEST, message);
    }
}
