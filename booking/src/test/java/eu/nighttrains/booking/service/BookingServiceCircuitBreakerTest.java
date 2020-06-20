package eu.nighttrains.booking.service;

import eu.nighttrains.booking.dto.BookingDto;
import eu.nighttrains.booking.model.BookingStatus;
import eu.nighttrains.booking.service.config.CircuitBreakerTestConfig;
import eu.nighttrains.booking.service.config.RabbitMqMockConfig;
import eu.nighttrains.booking.service.rest.TimeTableRestClientFeign;
import eu.nighttrains.timetable.model.TrainCarType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.mockito.internal.stubbing.answers.Returns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import({CircuitBreakerTestConfig.class, RabbitMqMockConfig.class})
public class BookingServiceCircuitBreakerTest {
	@MockBean(name = "timeTableRestClientFeign")
	private TimeTableRestClientFeign timeTableRestClient;

	@Autowired
	private BookingService bookingService;

	@Test
	void testBookTicketWithNonCriticalTimeout() {
		this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);
		this.mockTrainCarsLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);

		assertDoesNotThrow(() -> {
			BookingDto booking = this.bookingService.book(0, 14, LocalDate.of(2020, 5, 10), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
			assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
		});
	}

	private void mockRailwayConnectionLookup(
			final int sleepTimeInMilliseconds
	) {
		Mockito.doAnswer(new AnswersWithDelay(
				sleepTimeInMilliseconds,
				new Returns(TimeTableMockJsonDataMapper.readRailwayStationConnections(0L, 14L))
		))
				.when(this.timeTableRestClient)
				.getRailwayConnections(0L, 14L);
	}

	void mockTrainCarsLookup(final int sleepTimeInMilliseconds) {
		Mockito.doAnswer(new AnswersWithDelay(
				sleepTimeInMilliseconds,
				new Returns(TimeTableMockJsonDataMapper.readTrainCarsForConnection(0))
		))
				.when(this.timeTableRestClient)
				.getTrainCarsForConnectionId((long) 0);
	}

	@Test
	void testBookTicketWithCriticalTimeout() {
		this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.CRITICAL_DELAY);
		this.mockTrainCarsLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);
		assertDoesNotThrow(() -> {
			BookingDto booking = this.bookingService.book(0, 14, LocalDate.of(2020, 5, 10), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
			assertEquals(BookingStatus.RESERVED, booking.getStatus());
		});

		this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);
		this.mockTrainCarsLookup(CircuitBreakerTestConfig.CRITICAL_DELAY);
		assertDoesNotThrow(() -> {
			BookingDto booking = this.bookingService.book(0, 14, LocalDate.of(2020, 5, 10), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
			assertEquals(BookingStatus.RESERVED, booking.getStatus());
		});
	}

	@Test
	void testBookTicketsWithCriticalTimeout() {
		this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.CRITICAL_DELAY);
		this.mockTrainCarsLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);
		assertDoesNotThrow(() -> {
			for (int index = 0; index < 10; ++index) {
				BookingDto booking = this.bookingService.book(0, 14, LocalDate.of(2020, 5, 11), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
				assertEquals(BookingStatus.RESERVED, booking.getStatus());
			}
		});

		this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);
		this.mockTrainCarsLookup(CircuitBreakerTestConfig.CRITICAL_DELAY);
		assertDoesNotThrow(() -> {
			for (int index = 0; index < 10; ++index) {
				BookingDto booking = this.bookingService.book(0, 14, LocalDate.of(2020, 5, 12), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
				assertEquals(BookingStatus.RESERVED, booking.getStatus());
			}
		});

		this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);
		this.mockTrainCarsLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);
		assertDoesNotThrow(() -> {
			BookingDto booking = this.bookingService.book(0, 14, LocalDate.of(2020, 5, 13), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
			assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
		});
	}

	@Test
	void testUpdateOpenBookings() {
		this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);
		this.mockTrainCarsLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);
		assertDoesNotThrow(() -> {
			for (int index = 0; index < 5; ++index) {
				BookingDto booking = this.bookingService.book(0, 14, LocalDate.of(2020, 5, 17), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
				assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
			}
		});

		this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.CRITICAL_DELAY);
		List<String> bookingIds = new ArrayList<>();
		assertDoesNotThrow(() -> {
			for (int index = 0; index < 5; ++index) {
				BookingDto booking = this.bookingService.book(0, 14, LocalDate.of(2020, 5, 17), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
				assertEquals(BookingStatus.RESERVED, booking.getStatus());
				bookingIds.add(booking.getId());
			}
			assertTrue(this.bookingService.findAllOpenBookings().size() > 0);
		});

		this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);
		this.mockTrainCarsLookup(CircuitBreakerTestConfig.CRITICAL_DELAY);
		this.bookingService.updateOpenBookingsStatus();
		assertTrue(this.bookingService.findAllOpenBookings().size() > 0);
		this.checkBookingStatusConfirmsTo(bookingIds, BookingStatus.RESERVED);

		this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.CRITICAL_DELAY);
		this.mockTrainCarsLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);
		this.bookingService.updateOpenBookingsStatus();
		assertTrue(this.bookingService.findAllOpenBookings().size() > 0);
		this.checkBookingStatusConfirmsTo(bookingIds, BookingStatus.RESERVED);

		this.mockRailwayConnectionLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);
		this.mockTrainCarsLookup(CircuitBreakerTestConfig.NON_CRITICAL_DELAY);
		this.bookingService.updateOpenBookingsStatus();
		assertEquals(0, this.bookingService.findAllOpenBookings().size());
		this.checkBookingStatusConfirmsTo(bookingIds, BookingStatus.CONFIRMED);
	}

	private void checkBookingStatusConfirmsTo(List<String> bookingIds, BookingStatus status) {
		assertDoesNotThrow(() -> {
			for (String bookingId : bookingIds) {
				BookingDto booking = this.bookingService.findById(bookingId);
				assertEquals(status, booking.getStatus());
			}
		});
	}
}
