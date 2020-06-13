package eu.nighttrains.booking.service;

public class UnauthorizedBookingAccess extends Exception {
	public UnauthorizedBookingAccess() {
	}

	public UnauthorizedBookingAccess(String message) {
		super(message);
	}

	public UnauthorizedBookingAccess(String message, Throwable cause) {
		super(message, cause);
	}

	public UnauthorizedBookingAccess(Throwable cause) {
		super(cause);
	}
}
