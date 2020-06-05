package eu.nighttrains.booking.service;

public class BookingNotFoundException extends Exception {
    public BookingNotFoundException() {
    }

    public BookingNotFoundException(String bookingId) {
        super("Booking " + bookingId + " does not exist");
    }

    public BookingNotFoundException(String bookingId, Throwable cause) {
        super("Booking " + bookingId + " does not exist", cause);
    }

    public BookingNotFoundException(Throwable cause) {
        super(cause);
    }
}
