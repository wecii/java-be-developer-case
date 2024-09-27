package org.byesildal.inghubsbe.order.exception;

public class AssetNotExistException extends RuntimeException {
    private String message;
    public AssetNotExistException(String message) {
        super(message);
        this.message = message;
    }
}
