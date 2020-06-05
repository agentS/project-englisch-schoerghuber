package eu.nighttrains.booking.service;

public class TimeTableServiceNotAvailableException extends RuntimeException {
	public TimeTableServiceNotAvailableException() {
	}

	public TimeTableServiceNotAvailableException(String message) {
		super(message);
	}

	public TimeTableServiceNotAvailableException(String message, Throwable cause) {
		super(message, cause);
	}

	public TimeTableServiceNotAvailableException(Throwable cause) {
		super(cause);
	}
}
