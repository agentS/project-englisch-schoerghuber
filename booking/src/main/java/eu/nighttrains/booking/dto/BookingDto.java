package eu.nighttrains.booking.dto;

import eu.nighttrains.booking.model.BookingStatus;
import eu.nighttrains.timetable.model.TrainCarType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BookingDto {
    private String id;
    private long departureStationId;
    private long arrivalStationId;
    private LocalDate departureDate;
    private TrainCarType trainCarType;
    private BookingStatus status;
    private List<TicketDto> tickets;

    public BookingDto(String id, long departureStationId, long arrivalStationId, LocalDate departureDate, TrainCarType trainCarType, BookingStatus status) {
        this(id, departureStationId, arrivalStationId, departureDate, trainCarType, status, new ArrayList<>());
    }

    public BookingDto(String id, long departureStationId, long arrivalStationId, LocalDate departureDate, TrainCarType trainCarType, BookingStatus status, List<TicketDto> tickets) {
        this.id = id;
        this.departureStationId = departureStationId;
        this.arrivalStationId = arrivalStationId;
        this.departureDate = departureDate;
        this.trainCarType = trainCarType;
        this.status = status;
        this.tickets = tickets;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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

    public LocalDate getDepartureDate() {
        return this.departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public TrainCarType getTrainCarType() {
        return this.trainCarType;
    }

    public void setTrainCarType(TrainCarType trainCarType) {
        this.trainCarType = trainCarType;
    }

    public BookingStatus getStatus() {
        return this.status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public List<TicketDto> getTickets() {
        return this.tickets;
    }

    public void setTickets(List<TicketDto> tickets) {
        this.tickets = tickets;
    }
}
