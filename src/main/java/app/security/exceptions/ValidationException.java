package app.security.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
}
