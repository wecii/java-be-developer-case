package org.byesildal.inghubsbe.wallet.exception;

public class DuplicateException extends RuntimeException {
    private String message;
    public DuplicateException(String message) {
        super(message);
        this.message = message;
    }
}
