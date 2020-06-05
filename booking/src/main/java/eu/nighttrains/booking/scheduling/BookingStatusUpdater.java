package eu.nighttrains.booking.scheduling;

import eu.nighttrains.booking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingStatusUpdater {
	private final BookingService bookingService;

	private static final Logger logger = LoggerFactory.getLogger(BookingStatusUpdater.class);

	public BookingStatusUpdater(
			@Autowired BookingService bookingService
	) {
		this.bookingService = bookingService;
	}

	@Scheduled(fixedRateString = "${bookingstatusupdate.interval}")
	public void updateBookingStatuses() {
		logger.info("Updating statuses of open bookings.");
		this.bookingService.updateOpenBookingsStatus();
	}
}
