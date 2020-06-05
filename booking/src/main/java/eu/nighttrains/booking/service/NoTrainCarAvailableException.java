package eu.nighttrains.booking.service;

public class NoTrainCarAvailableException extends Exception {
    public NoTrainCarAvailableException(String message) {
        super(message);
    }

    public NoTrainCarAvailableException(String message, Exception cause){
        super(message, cause);
    }
}
