package eu.nighttrains.booking.dal;

import eu.nighttrains.booking.model.Booking;
import eu.nighttrains.booking.model.BookingStatus;
import eu.nighttrains.booking.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
	@Query(
			value = "{ 'tickets.departureStationIds': { $in: ?0 }, 'tickets.departureDate': ?1, 'tickets.trainConnectionId': ?2, 'tickets.trainCarId': { $in: ?3 } }",
			fields = "{ tickets: 1 }"
	)
	List<Booking> findAllBookingsWithTicketsBetween(
			Set<Long> departureStationIds,
			LocalDate ticketDepartureDate,
			Long trainCarId,
			Set<Long> trainCarIdsOfDesiredTrainCarType
	);

	default List<Ticket> findAllTicketsBetween(
			Set<Long> departureStationIds,
			LocalDate ticketDepartureDate,
			Long trainCarId,
			Set<Long> trainCarIdsOfDesiredTrainCarType
	) {
		return findAllBookingsWithTicketsBetween(
				departureStationIds,
				ticketDepartureDate,
				trainCarId,
				trainCarIdsOfDesiredTrainCarType
		).stream()
				.map(booking -> booking.getTickets().get(0))
				.collect(Collectors.toList());
	}

	List<Booking> findAllByStatus(BookingStatus status);
}
