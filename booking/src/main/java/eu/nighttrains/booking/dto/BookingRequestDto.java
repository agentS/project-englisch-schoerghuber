package eu.nighttrains.booking.dto;

import eu.nighttrains.timetable.model.TrainCarType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

public class BookingRequestDto {

    @PositiveOrZero
    @Schema(required = true)
    private long departureStationId;

    @PositiveOrZero
    @Schema(required = true)
    private long arrivalStationId;

    @Schema(required = true)
    private LocalDate departureDate;

    //@NotNull
    @Schema(required = true)
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
