package app.security.exceptions;

public class NotAuthorizedException extends RuntimeException {
    private final int statusCode;
    public NotAuthorizedException(int statusCode, String message) { super(message); this.statusCode = statusCode; }
    public int getStatusCode() { return statusCode; }
}
