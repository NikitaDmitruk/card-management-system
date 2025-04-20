package by.testprojects.cardmanagementsystem.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class UserNotFoundException extends AbstractThrowableProblem {
    private static final URI TYPE = URI.create("https://api.company.com/api/problems/user-not-found");

    public UserNotFoundException(Long id) {
        super(TYPE, "User not found", Status.NOT_FOUND,
                String.format("User with id '%s' not found", id));
    }

}
