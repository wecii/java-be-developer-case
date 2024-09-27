package org.byesildal.inghubsbe.order.exception;

public class WalletNotExistException extends RuntimeException {
    private String message;
    public WalletNotExistException(String message) {
        super(message);
        this.message = message;
    }
}
