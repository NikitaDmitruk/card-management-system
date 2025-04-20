package by.testprojects.cardmanagementsystem.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.net.URI;

// Ошибка шифрования
public class CardEncryptionException extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("https://api.company.com/problems/encryption-error");

    public CardEncryptionException(String operation, Throwable cause) {
        super(TYPE, "Encryption Failed", Status.INTERNAL_SERVER_ERROR,
                String.format("%s operation failed", operation), null,
                cause != null ? Problem.builder()
                        .withDetail(cause.getMessage())
                        .build() : null);
    }
}