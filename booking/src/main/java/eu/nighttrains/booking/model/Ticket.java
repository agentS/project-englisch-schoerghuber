package eu.nighttrains.booking.model;

import java.time.LocalDate;
import java.util.Set;

public class Ticket {
    private Long originStationId;

    private Long destinationStationId;

    private Long trainCarId;

    private Integer placeNumber;

    private LocalDate departureDate;

    private Long trainConnectionId;

    private Set<Long> departureStationIds;

    public Ticket() {}

    public Ticket(Long originStationId, Long destinationStationId, Long trainCarId, Integer placeNumber, LocalDate departureDate, Long trainConnectionId, Set<Long> departureStationIds) {
        this.originStationId = originStationId;
        this.destinationStationId = destinationStationId;
        this.trainCarId = trainCarId;
        this.placeNumber = placeNumber;
        this.departureDate = departureDate;
        this.trainConnectionId = trainConnectionId;
        this.departureStationIds = departureStationIds;
    }

    public Long getOriginStationId() {
        return this.originStationId;
    }

    public void setOriginStationId(Long originStationId) {
        this.originStationId = originStationId;
    }

    public Long getDestinationStationId() {
        return this.destinationStationId;
    }

    public void setDestinationStationId(Long destinationStationId) {
        this.destinationStationId = destinationStationId;
    }

    public Long getTrainCarId() {
        return this.trainCarId;
    }

    public void setTrainCarId(Long trainCarId) {
        this.trainCarId = trainCarId;
    }

    public Integer getPlaceNumber() {
        return this.placeNumber;
    }

    public void setPlaceNumber(Integer placeNumber) {
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
