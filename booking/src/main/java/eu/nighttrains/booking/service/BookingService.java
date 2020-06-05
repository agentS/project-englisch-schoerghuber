package eu.nighttrains.booking.service;

import eu.nighttrains.booking.dto.BookingDto;
import eu.nighttrains.timetable.model.TrainCarType;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingDto findById(String id) throws BookingNotFoundException;
    BookingDto book(
            final long departureStationId, final long arrivalStationId,
            LocalDate departureDate,
            TrainCarType trainCarType
    );

    List<BookingDto> findAllOpenBookings();
    void updateOpenBookingsStatus();
}
