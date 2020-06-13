package eu.nighttrains.booking.notification;

import eu.nighttrains.booking.model.Booking;

public interface BookingNotificationPublisher {
	void publishBookingStatusUpdate(Booking booking);
}
