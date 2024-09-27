package org.byesildal.inghubsbe.wallet.exception;

public class ForbiddenException extends RuntimeException {
    private String message;
    public ForbiddenException(String message) {
        super(message);
        this.message = message;
    }
}
