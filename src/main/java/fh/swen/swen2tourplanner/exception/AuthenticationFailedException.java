package fh.swen.swen2tourplanner.exception;

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
