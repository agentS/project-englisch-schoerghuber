package eu.nighttrains.booking.dal;

import eu.nighttrains.booking.model.Booking;
import eu.nighttrains.booking.model.BookingStatus;
import eu.nighttrains.booking.model.Ticket;
import eu.nighttrains.timetable.model.TrainCarType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@DataMongoTest
class BookingRepositoryTest {
	@Autowired
	private BookingRepository bookingRepository;

	@Test
	void testFindAllTicketsBetweenStations() {
		List<Ticket> tickets = new ArrayList<>();
		tickets.add(new Ticket(
				14L, 0L,
				20L, 1,
				LocalDate.of(2020, 5, 29),
				210L,
				LongStream.range(14, 0L).boxed().collect(Collectors.toSet())
		));
		this.bookingRepository.save(new Booking(
				14L, 0L,
				LocalDate.of(2020, 5, 29),
				TrainCarType.SLEEPER,
				BookingStatus.CONFIRMED,
				tickets
		));

		tickets = new ArrayList<>();
		tickets.add(new Ticket(
				0L, 14L,
				20L, 1,
				LocalDate.of(2020, 5, 29),
				200L,
				LongStream.range(0, 14L).boxed().collect(Collectors.toSet())
		));
		this.bookingRepository.save(new Booking(
				0L, 14L,
				LocalDate.of(2020, 5, 29),
				TrainCarType.SLEEPER,
				BookingStatus.CONFIRMED,
				tickets
		));

		tickets = new ArrayList<>();
		tickets.add(new Ticket(
				2L, 10L,
				20L, 2,
				LocalDate.of(2020, 5, 29),
				200L,
				LongStream.range(2, 10).boxed().collect(Collectors.toSet())
		));
		this.bookingRepository.save(new Booking(
				2L, 10L,
				LocalDate.of(2020, 5, 29),
				TrainCarType.SLEEPER,
				BookingStatus.CONFIRMED,
				tickets
		));

		List<Ticket> foundTickets = this.bookingRepository.findAllTicketsBetween(
				LongStream.range(0, 14L).boxed().collect(Collectors.toSet()),
				LocalDate.of(2020, 5, 29),
				200L,
				LongStream.rangeClosed(20L, 21L).boxed().collect(Collectors.toSet())
		);
		assertNotNull(foundTickets);
		assertEquals(2, foundTickets.size());

		foundTickets = this.bookingRepository.findAllTicketsBetween(
				LongStream.range(1, 12L).boxed().collect(Collectors.toSet()),
				LocalDate.of(2020, 5, 29),
				200L,
				LongStream.rangeClosed(20L, 21L).boxed().collect(Collectors.toSet())
		);
		assertNotNull(foundTickets);
		assertEquals(2, foundTickets.size());

		foundTickets = this.bookingRepository.findAllTicketsBetween(
				LongStream.range(3, 9L).boxed().collect(Collectors.toSet()),
				LocalDate.of(2020, 5, 29),
				200L,
				LongStream.rangeClosed(20L, 21L).boxed().collect(Collectors.toSet())
		);
		assertNotNull(foundTickets);
		assertEquals(2, foundTickets.size());

		foundTickets = this.bookingRepository.findAllTicketsBetween(
				LongStream.range(0, 2L).boxed().collect(Collectors.toSet()),
				LocalDate.of(2020, 5, 29),
				200L,
				LongStream.rangeClosed(20L, 21L).boxed().collect(Collectors.toSet())
		);
		assertNotNull(foundTickets);
		assertEquals(1, foundTickets.size());

		foundTickets = this.bookingRepository.findAllTicketsBetween(
				LongStream.range(3, 9L).boxed().collect(Collectors.toSet()),
				LocalDate.of(2020, 5, 29),
				200L,
				LongStream.rangeClosed(24L, 25L).boxed().collect(Collectors.toSet())
		);
		assertNotNull(foundTickets);
		assertEquals(0, foundTickets.size());
	}

	@Test
	void testFindAllOpenBookings() {
		List<Ticket> tickets = new ArrayList<>();
		Booking openBooking = this.bookingRepository.save(new Booking(
				14L, 0L,
				LocalDate.of(2020, 5, 29),
				TrainCarType.COUCHETTE,
				BookingStatus.RESERVED,
				tickets
		));

		tickets = new ArrayList<>();
		tickets.add(new Ticket(
				14L, 0L,
				20L, 1,
				LocalDate.of(2020, 5, 29),
				210L,
				LongStream.range(14, 0L).boxed().collect(Collectors.toSet())
		));
		this.bookingRepository.save(new Booking(
				14L, 0L,
				LocalDate.of(2020, 5, 29),
				TrainCarType.COUCHETTE,
				BookingStatus.CONFIRMED,
				tickets
		));

		List<Booking> openBookingList = this.bookingRepository.findAllByStatus(BookingStatus.RESERVED);
		assertEquals(1, openBookingList.size());

		openBooking.setStatus(BookingStatus.REJECTED);
		openBooking = this.bookingRepository.save(openBooking);
		openBookingList = this.bookingRepository.findAllByStatus(BookingStatus.RESERVED);
		assertEquals(0, openBookingList.size());
	}
}
