package eu.nighttrains.booking.service;

import eu.nighttrains.booking.dto.BookingDto;
import eu.nighttrains.booking.model.BookingStatus;
import eu.nighttrains.booking.service.config.CircuitBreakerTestConfig;
import eu.nighttrains.booking.service.rest.TimeTableRestClientFeign;
import eu.nighttrains.timetable.model.TrainCarType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(CircuitBreakerTestConfig.class)
public class BookingServiceOpenBookingsRejectionTest {
	private static final int SLEEPER_CARS_MAXIMUM_CAPACITY = 40;

	@MockBean(name = "timeTableRestClientFeign")
	private TimeTableRestClientFeign timeTableRestClient;

	@Autowired
	private BookingService bookingService;

	@Test
	void testUpdateOpenBookingsWithOverbooking() {
		this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY, 0, 14);
		this.mockTrainCarsLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY, 0);
		assertDoesNotThrow(() -> {
			for (int index = 0; index < SLEEPER_CARS_MAXIMUM_CAPACITY; ++index) {
				BookingDto booking = this.bookingService.book(0, 14, LocalDate.of(2020, 5, 1), TrainCarType.SLEEPER);
				assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
			}
		});

		this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.CRITICAL_DELAY, 0, 14);
		this.mockTrainCarsLookup(CircuitBreakerTestConfig.CRITICAL_DELAY, 0);
		assertDoesNotThrow(() -> {
			BookingDto booking = this.bookingService.book(0, 14, LocalDate.of(2020, 5, 1), TrainCarType.SLEEPER);
			assertEquals(BookingStatus.RESERVED, booking.getStatus());

			this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY, 0, 14);
			this.mockTrainCarsLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY, 0);
			this.bookingService.updateOpenBookingsStatus();

			BookingDto rejectedBooking = this.bookingService.findById(booking.getId());
			assertEquals(BookingStatus.REJECTED, rejectedBooking.getStatus());
		});
	}

	private void mockRailwayConnectionLookup(
			final int sleepTimeInMilliseconds,
			final long departureStationId, final long arrivalStationId
	) {
		Mockito.doAnswer(new AnswersWithDelay(
				sleepTimeInMilliseconds,
				new Returns(TimeTableMockJsonDataMapper.readRailwayStationConnections(departureStationId, arrivalStationId))
		))
				.when(this.timeTableRestClient)
				.getRailwayConnections(departureStationId, arrivalStationId);
	}

	void mockTrainCarsLookup(final int sleepTimeInMilliseconds, final long trainCarId) {
		Mockito.doAnswer(new AnswersWithDelay(
				sleepTimeInMilliseconds,
				new Returns(TimeTableMockJsonDataMapper.readTrainCarsForConnection(trainCarId))
		))
				.when(this.timeTableRestClient)
				.getTrainCarsForConnectionId(trainCarId);
	}

	@Test
	void testRejectBookingForInvalidRoute() {
		this.mockInvalidRailwayConnectionLookup(CircuitBreakerTestConfig.CRITICAL_DELAY, 0, -10);

		BookingDto invalidBooking = this.bookingService.book(0, -10, LocalDate.of(2020, 5, 1), TrainCarType.SLEEPER);
		assertEquals(BookingStatus.RESERVED, invalidBooking.getStatus());

		this.mockInvalidRailwayConnectionLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY, 0, -10);
		this.bookingService.updateOpenBookingsStatus();

		assertThrows(BookingNotFoundException.class, () -> this.bookingService.findById(invalidBooking.getId()));
	}

	void mockInvalidRailwayConnectionLookup(
			final int sleepTimeInMilliseconds,
			final long departureStationId, final long arrivalStationId
	) {
		Mockito.doAnswer(new AnswersWithDelay(
				sleepTimeInMilliseconds,
				new ThrowsException(new NoConnectionsAvailableException())
		))
				.when(this.timeTableRestClient)
				.getRailwayConnections(departureStationId, arrivalStationId);
	}
}
