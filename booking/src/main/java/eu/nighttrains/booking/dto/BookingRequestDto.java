package eu.nighttrains.booking.dto;

import eu.nighttrains.timetable.model.TrainCarType;

import java.time.LocalDate;

public class BookingRequestDto {
    private long departureStationId;
    private long arrivalStationId;
    private LocalDate departureDate;
    private TrainCarType trainCarType;

    public BookingRequestDto() {
    }

    public long getDepartureStationId() {
        return departureStationId;
    }

    public long getArrivalStationId() {
        return arrivalStationId;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public TrainCarType getTrainCarType() {
        return trainCarType;
    }
}
