package eu.nighttrains.booking.model;

import eu.nighttrains.timetable.model.TrainCarType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document("booking")
public class Booking {
    @Id
    private String id;

    private Long departureStationId;

    private Long arrivalStationId;

    private LocalDate departureDate;

    private TrainCarType trainCarType;

    private BookingStatus status;

    private List<Ticket> tickets = new ArrayList<>();

    public Booking() {}

    public Booking(Long departureStationId, Long arrivalStationId, LocalDate departureDate, TrainCarType trainCarType, BookingStatus status, List<Ticket> tickets) {
        this.departureStationId = departureStationId;
        this.arrivalStationId = arrivalStationId;
        this.departureDate = departureDate;
        this.trainCarType = trainCarType;
        this.status = status;
        this.setTickets(tickets);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDepartureStationId() {
        return this.departureStationId;
    }

    public void setDepartureStationId(Long originId) {
        this.departureStationId = originId;
    }

    public Long getArrivalStationId() {
        return this.arrivalStationId;
    }

    public void setArrivalStationId(Long destinationId) {
        this.arrivalStationId = destinationId;
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

    public List<Ticket> getTickets() {
        return this.tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
