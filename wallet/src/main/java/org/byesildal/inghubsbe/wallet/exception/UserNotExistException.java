package org.byesildal.inghubsbe.wallet.exception;

public class UserNotExistException extends RuntimeException {
    private String message;
    public UserNotExistException(String message) {
        super(message);
        this.message = message;
    }
}
