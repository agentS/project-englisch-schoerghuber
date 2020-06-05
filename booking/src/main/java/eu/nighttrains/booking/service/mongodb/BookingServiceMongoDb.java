package eu.nighttrains.booking.service.mongodb;

import eu.nighttrains.booking.dal.BookingRepository;
import eu.nighttrains.booking.model.Booking;
import eu.nighttrains.booking.service.*;
import eu.nighttrains.booking.dto.BookingDto;
import eu.nighttrains.booking.dto.TicketDto;
import eu.nighttrains.booking.model.BookingStatus;
import eu.nighttrains.booking.model.Ticket;
import eu.nighttrains.timetable.dto.RailwayStationConnectionDto;
import eu.nighttrains.timetable.dto.TrainCarDto;
import eu.nighttrains.timetable.dto.TrainConnectionDto;
import eu.nighttrains.timetable.model.TrainCarType;
import io.vavr.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceMongoDb implements BookingService {
    private static BookingDto mapBooking(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getDepartureStationId(), booking.getArrivalStationId(),
                booking.getDepartureDate(),
                booking.getTrainCarType(),
                booking.getStatus(),
                booking.getTickets().stream()
                    .map(BookingServiceMongoDb::mapTicket)
                    .collect(Collectors.toList())
        );
    }

    private static TicketDto mapTicket(Ticket ticket) {
        return new TicketDto(
                ticket.getOriginStationId(), ticket.getDestinationStationId(),
                ticket.getTrainCarId(),
                ticket.getPlaceNumber(),
                ticket.getDepartureDate(),
                ticket.getTrainConnectionId(),
                ticket.getDepartureStationIds()
        );
    }

    private final BookingRepository bookingRepository;
    private final TimeTableService timeTableClient;

    public BookingServiceMongoDb(
            @Autowired BookingRepository bookingRepository,
            @Autowired TimeTableService timeTableClient
    ) {
        this.bookingRepository = bookingRepository;
        this.timeTableClient = timeTableClient;
    }

    @Override
    public BookingDto findById(String id) throws BookingNotFoundException {
        return mapBooking(this.bookingRepository.findById(id).orElseThrow(
                () -> new BookingNotFoundException(id)
        ));
    }

    @Override
    public BookingDto book(
            final long departureStationId, final long arrivalStationId,
            LocalDate departureDate,
            TrainCarType trainCarType
    ) {
        Booking addedBooking = new Booking(
                departureStationId, arrivalStationId, departureDate, trainCarType,
                BookingStatus.RESERVED, new ArrayList<>()
        );
        try {
            this.tryToBookTickets(addedBooking, trainCarType);
        } catch (TimeTableServiceNotAvailableException exception) {
            addedBooking.setStatus(BookingStatus.RESERVED);
        } catch (NoTrainCarAvailableException exception) {
            addedBooking.setStatus(BookingStatus.REJECTED);
        }
        addedBooking = this.bookingRepository.save(addedBooking);
        return mapBooking(addedBooking);
    }

    private void tryToBookTickets(Booking addedBooking, TrainCarType trainCarType)
        throws NoConnectionsAvailableException, NoTrainCarAvailableException {
        List<RailwayStationConnectionDto> connections = this.timeTableClient.getRailwayConnections(
                addedBooking.getDepartureStationId(), addedBooking.getArrivalStationId()
        );
        Map<TrainConnectionDto, List<RailwayStationConnectionDto>> connectionsByChange = connections.stream()
                .collect(Collectors.groupingBy(RailwayStationConnectionDto::getTrainConnection));
        LocalDate ticketDepartureDate = LocalDate.of(
                addedBooking.getDepartureDate().getYear(),
                addedBooking.getDepartureDate().getMonthValue(),
                addedBooking.getDepartureDate().getDayOfMonth()
        );
        for (TrainConnectionDto trainConnection : connectionsByChange.keySet()) {
            trainConnection.setTrainCars(
                    this.timeTableClient.getTrainCarsForConnectionId(trainConnection.getId())
            );

            List<RailwayStationConnectionDto> stopsOfConnection =
                    connectionsByChange.get(trainConnection);
            Set<Long> departureStationIds = stopsOfConnection.stream()
                    .map(connection -> connection.getDepartureStation().getId())
                    .collect(Collectors.toSet());
            Set<Long> trainCarIdsOfDesiredType = trainConnection.getTrainCars().stream()
                    .filter(trainCar -> Objects.equals(trainCar.getType(), trainCarType))
                    .map(TrainCarDto::getId)
                    .collect(Collectors.toSet());
            List<Ticket> alreadyBookedTickets = this.bookingRepository.findAllTicketsBetween(
                    departureStationIds,
                    ticketDepartureDate,
                    trainConnection.getId(),
                    trainCarIdsOfDesiredType
            );
            final Tuple2<Long, Integer> trainCarIdAndPlace = this.findTrainCarWithFreePlace(
                    trainConnection, alreadyBookedTickets, trainCarType
            );
            if (!Objects.equals(trainCarIdAndPlace._1(), (-1L))) {
                Ticket bookedTicket = new Ticket(
                        stopsOfConnection.get(0).getDepartureStation().getId(),
                        stopsOfConnection.get(stopsOfConnection.size() - 1).getArrivalStation().getId(),
                        trainCarIdAndPlace._1(),
                        trainCarIdAndPlace._2(),
                        ticketDepartureDate,
                        trainConnection.getId(),
                        stopsOfConnection.stream()
                                .map(station -> station.getDepartureStation().getId())
                                .collect(Collectors.toSet())
                );
                addedBooking.getTickets().add(bookedTicket);
            } else {
                throw new NoTrainCarAvailableException("No place in the desired category available for train connection " + trainConnection.getCode());
            }

            ticketDepartureDate = ticketDepartureDate.plusDays(
                    JourneyDurationCalculator.calculateJourneyDurationInDays(stopsOfConnection)
            );
        }

        if (addedBooking.getTickets().size() == connectionsByChange.keySet().size()) {
            addedBooking.setStatus(BookingStatus.CONFIRMED);
        }
    }

    private Tuple2<Long, Integer> findTrainCarWithFreePlace(
            TrainConnectionDto trainConnection,
            List<Ticket> alreadyBookedTickets,
            TrainCarType trainCarType
    ) {
        List<TrainCarDto> trainCarsOfDesiredCategory = trainConnection.getTrainCars().stream()
                .filter(trainCar -> Objects.equals(trainCar.getType(), trainCarType))
                .collect(Collectors.toList());
        for (TrainCarDto trainCar : trainCarsOfDesiredCategory) {
            final long numberOfPurchasedTicketsInTrainCar = alreadyBookedTickets.stream()
                    .filter(ticket -> Objects.equals(ticket.getTrainCarId(), trainCar.getId()))
                    .count();
            if (numberOfPurchasedTicketsInTrainCar < trainCar.getCapacity()) {
                return new Tuple2<>(
                        trainCar.getId(),
                        ((int) (numberOfPurchasedTicketsInTrainCar + 1))
                );
            }
        }
        return new Tuple2<>(-1L , -1);
    }

    @Override
    public List<BookingDto> findAllOpenBookings() {
        return this.bookingRepository.findAllByStatus(BookingStatus.RESERVED).stream()
                .map(BookingServiceMongoDb::mapBooking)
                .collect(Collectors.toList());
    }

    @Override
    public void updateOpenBookingsStatus() {
        List<Booking> openBookings = this.bookingRepository.findAllByStatus(BookingStatus.RESERVED);
        for (Booking openBooking : openBookings) {
            try {
                this.tryToBookTickets(openBooking, openBooking.getTrainCarType());
                this.bookingRepository.save(openBooking);
            } catch (TimeTableServiceNotAvailableException exception) {
                openBooking.setStatus(BookingStatus.RESERVED);
            } catch (NoTrainCarAvailableException exception) {
                openBooking.setStatus(BookingStatus.REJECTED);
                this.bookingRepository.save(openBooking);
            } catch (NoConnectionsAvailableException exception) {
                this.bookingRepository.delete(openBooking);
            }
        }
    }
}
