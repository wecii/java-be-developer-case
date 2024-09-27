package org.byesildal.inghubsbe.order.exception;

public class UserNotExistException extends RuntimeException {
    private String message;
    public UserNotExistException(String message) {
        super(message);
        this.message = message;
    }
}
