package org.byesildal.inghubsbe.order.exception;

public class InsufficientBalanceException extends RuntimeException {
    private String message;
    public InsufficientBalanceException(String message) {
        super(message);
        this.message = message;
    }
}
