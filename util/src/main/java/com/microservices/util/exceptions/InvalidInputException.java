package com.microservices.util.exceptions;

public class InvalidInputException extends RuntimeException {

    private static final long serialVersionUID = -5755556421008389609L;

    public InvalidInputException() {
    }

    public InvalidInputException(final String message) {
        super(message);
    }

    public InvalidInputException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidInputException(final Throwable cause) {
        super(cause);
    }
}
