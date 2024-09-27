package org.byesildal.inghubsbe.wallet.exception;

public class AssetNotExistException extends RuntimeException {
    private String message;
    public AssetNotExistException(String message) {
        super(message);
        this.message = message;
    }
}
