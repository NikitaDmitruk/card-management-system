package by.testprojects.cardmanagementsystem.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.UUID;

public class CardNotFoundException extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("https://api.bank.com/problems/card-not-found");

    public CardNotFoundException(UUID id) {
        super(TYPE, "Card not found", Status.NOT_FOUND,
                String.format("Card with id '%s' not found", id));
    }
}
