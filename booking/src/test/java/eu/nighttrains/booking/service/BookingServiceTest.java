package eu.nighttrains.booking.service;

import eu.nighttrains.booking.dto.BookingDto;
import eu.nighttrains.booking.model.BookingStatus;
import eu.nighttrains.booking.service.config.CircuitBreakerTestConfig;
import eu.nighttrains.booking.service.config.RabbitMqMockConfig;
import eu.nighttrains.booking.service.rest.TimeTableRestClientFeign;
import eu.nighttrains.timetable.model.TrainCarType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import({CircuitBreakerTestConfig.class, RabbitMqMockConfig.class})
class BookingServiceTest {
	@MockBean(name = "timeTableRestClientFeign")
	private TimeTableRestClientFeign timeTableRestClient;

	@Autowired
	private BookingService bookingService;

	@Test
	void createBooking() {
		Mockito.when(this.timeTableRestClient.getRailwayConnections(0L, 14L))
				.thenReturn(TimeTableMockJsonDataMapper.readRailwayStationConnections(0L, 14L));
		Mockito.when(this.timeTableRestClient.getTrainCarsForConnectionId(0L))
				.thenReturn(TimeTableMockJsonDataMapper.readTrainCarsForConnection(0L));

		assertDoesNotThrow(() -> {
			BookingDto addedBooking = this.bookingService.book(0, 14, LocalDate.of(2020, 5, 29), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
			assertNotNull(addedBooking);
			assertNotNull(addedBooking.getId());
			assertNotEquals("", addedBooking.getId());
			assertEquals(1, addedBooking.getTickets().size());
			assertEquals(BookingStatus.CONFIRMED, addedBooking.getStatus());

			BookingDto lookedUpBooking = this.bookingService.findById(addedBooking.getId());
			assertNotNull(lookedUpBooking);
			assertEquals(addedBooking.getId(), lookedUpBooking.getId());
			assertEquals(addedBooking.getTickets().size(), lookedUpBooking.getTickets().size());
			assertEquals(BookingStatus.CONFIRMED, lookedUpBooking.getStatus());

			assertEquals(
					LocalDate.of(2020, 5, 29),
					lookedUpBooking.getTickets().get(0).getDepartureDate()
			);
			assertEquals(1, addedBooking.getTickets().get(0).getPlaceNumber());
		});
	}

	@Test
	void createBookingWithChange() {
		Mockito.when(this.timeTableRestClient.getRailwayConnections(28L, 14L))
				.thenReturn(TimeTableMockJsonDataMapper.readRailwayStationConnections(28L, 14L));
		Mockito.when(this.timeTableRestClient.getTrainCarsForConnectionId(0L))
				.thenReturn(TimeTableMockJsonDataMapper.readTrainCarsForConnection(0L));
		Mockito.when(this.timeTableRestClient.getTrainCarsForConnectionId(3L))
				.thenReturn(TimeTableMockJsonDataMapper.readTrainCarsForConnection(3L));

		assertDoesNotThrow(() -> {
			BookingDto addedBooking = this.bookingService.book(28, 14, LocalDate.of(2020, 5, 30), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
			assertNotNull(addedBooking);
			assertNotNull(addedBooking.getId());
			assertNotEquals("", addedBooking.getId());
			assertEquals(2, addedBooking.getTickets().size());
			assertEquals(BookingStatus.CONFIRMED, addedBooking.getStatus());

			assertEquals(
					LocalDate.of(2020, 5, 30),
					addedBooking.getTickets().get(0).getDepartureDate()
			);
			assertEquals(
					LocalDate.of(2020, 5, 31),
					addedBooking.getTickets().get(1).getDepartureDate()
			);

			assertEquals(1, addedBooking.getTickets().get(0).getPlaceNumber());
			assertEquals(1, addedBooking.getTickets().get(1).getPlaceNumber());
		});
	}

	@Test
	void overbookTrain() {
		Mockito.when(this.timeTableRestClient.getRailwayConnections(0L, 14L))
				.thenReturn(TimeTableMockJsonDataMapper.readRailwayStationConnections(0L, 14L));
		Mockito.when(this.timeTableRestClient.getRailwayConnections(2L, 13L))
				.thenReturn(TimeTableMockJsonDataMapper.readRailwayStationConnections(2L, 13L));
		Mockito.when(this.timeTableRestClient.getRailwayConnections(28L, 14L))
				.thenReturn(TimeTableMockJsonDataMapper.readRailwayStationConnections(28L, 14L));
		Mockito.when(this.timeTableRestClient.getTrainCarsForConnectionId(0L))
				.thenReturn(TimeTableMockJsonDataMapper.readTrainCarsForConnection(0L));
		Mockito.when(this.timeTableRestClient.getTrainCarsForConnectionId(3L))
				.thenReturn(TimeTableMockJsonDataMapper.readTrainCarsForConnection(3L));

		assertDoesNotThrow(() -> {
			for (int index = 0; index < 40; ++index) {
				BookingDto booking = this.bookingService.book(2, 13, LocalDate.of(2020, 5, 20), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
				assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
			}
		});

		BookingDto booking = this.bookingService.book(0, 14, LocalDate.of(2020, 5, 20), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
		assertEquals(BookingStatus.REJECTED, booking.getStatus());

		assertDoesNotThrow(() -> {
			this.bookingService.book(28, 0, LocalDate.of(2020, 5, 20), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
		});
	}

	@Test
	void tryBookTicketOnNonExistingRoute() {
		Mockito.when(this.timeTableRestClient.getRailwayConnections(0L, -20L))
				.thenThrow(new NoConnectionsAvailableException());
		assertThrows(NoConnectionsAvailableException.class, () -> {
			this.bookingService.book(0, -20, LocalDate.of(2020, 5, 31), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
		});
	}

	@Test
	void testBookingAuthorization() {
		assertDoesNotThrow(() -> {
			final BookingDto booking = this.bookingService.book(0, 14, LocalDate.of(2020, 6, 13), TrainCarType.SLEEPER, IntegrationTestConstants.EMAIL_ADDRESS);
			final BookingDto lookedUpBooking = this.bookingService.findById(booking.getId(), IntegrationTestConstants.EMAIL_ADDRESS);
			assertEquals(booking.getEmailAddress(), lookedUpBooking.getEmailAddress());
			assertThrows(UnauthorizedBookingAccess.class, () -> {
				this.bookingService.findById(booking.getId(), "homer.simpson@burnspowerplant.com");
			});
		});
	}
}
