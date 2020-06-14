package eu.nighttrains.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.Set;

public final class TicketDto {

    @PositiveOrZero
    @Schema(required = true)
    private long departureStationId;

    @PositiveOrZero
    @Schema(required = true)
    private long arrivalStationId;

    @PositiveOrZero
    @Schema(required = true)
    private long trainCarId;

    @PositiveOrZero
    @Schema(required = true)
    private int placeNumber;

    @NotNull
    @Schema(required = true)
    private LocalDate departureDate;

    @PositiveOrZero
    @Schema(required = true)
    private Long trainConnectionId;

    private Set<Long> departureStationIds;

    public TicketDto(long departureStationId, long arrivalStationId, long trainCarId, int placeNumber, LocalDate departureDate, Long trainConnectionId, Set<Long> departureStationIds) {
        this.departureStationId = departureStationId;
        this.arrivalStationId = arrivalStationId;
        this.trainCarId = trainCarId;
        this.placeNumber = placeNumber;
        this.departureDate = departureDate;
        this.trainConnectionId = trainConnectionId;
        this.departureStationIds = departureStationIds;
    }

    public long getDepartureStationId() {
        return this.departureStationId;
    }

    public void setDepartureStationId(long departureStationId) {
        this.departureStationId = departureStationId;
    }

    public long getArrivalStationId() {
        return this.arrivalStationId;
    }

    public void setArrivalStationId(long arrivalStationId) {
        this.arrivalStationId = arrivalStationId;
    }

    public long getTrainCarId() {
        return this.trainCarId;
    }

    public void setTrainCarId(long trainCarId) {
        this.trainCarId = trainCarId;
    }

    public int getPlaceNumber() {
        return this.placeNumber;
    }

    public void setPlaceNumber(int placeNumber) {
        this.placeNumber = placeNumber;
    }

    public LocalDate getDepartureDate() {
        return this.departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public Long getTrainConnectionId() {
        return this.trainConnectionId;
    }

    public void setTrainConnectionId(Long trainConnectionId) {
        this.trainConnectionId = trainConnectionId;
    }

    public Set<Long> getDepartureStationIds() {
        return this.departureStationIds;
    }

    public void setDepartureStationIds(Set<Long> departureStationIds) {
        this.departureStationIds = departureStationIds;
    }
}
