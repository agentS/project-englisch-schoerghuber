package eu.nighttrains.booking.service;

public class NoConnectionsAvailableException extends RuntimeException {
    public NoConnectionsAvailableException() {
        super();
    }

    public NoConnectionsAvailableException(String message) {
        super(message);
    }

    public NoConnectionsAvailableException(String message, Exception cause){
        super(message, cause);
    }
}
