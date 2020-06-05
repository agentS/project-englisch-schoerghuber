package eu.nighttrains.timetable.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

public class RailwayStationConnectionDto {
    @Valid
    @NotNull
    private RailwayStationDto departureStation;

    @Valid
    @NotNull
    private RailwayStationDto arrivalStation;

    @Valid
    @NotNull
    private TrainConnectionDto trainConnection;

    @NotNull
    private LocalTime departureTime;

    @NotNull
    private LocalTime arrivalTime;

    public RailwayStationConnectionDto() {}

    public RailwayStationConnectionDto(RailwayStationDto departureStation, RailwayStationDto arrivalStation, TrainConnectionDto trainConnection, LocalTime departureTime, LocalTime arrivalTime) {
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.trainConnection = trainConnection;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public RailwayStationDto getDepartureStation() {
        return this.departureStation;
    }

    public void setDepartureStation(RailwayStationDto departureStation) {
        this.departureStation = departureStation;
    }

    public RailwayStationDto getArrivalStation() {
        return this.arrivalStation;
    }

    public void setArrivalStation(RailwayStationDto arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public TrainConnectionDto getTrainConnection() {
        return this.trainConnection;
    }

    public void setTrainConnection(TrainConnectionDto trainConnection) {
        this.trainConnection = trainConnection;
    }

    public LocalTime getDepartureTime() {
        return this.departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return this.arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public String toString() {
        return "RailwayStationConnectionDto{" +
                "departureStation=" + departureStation +
                ", arrivalStation=" + arrivalStation +
                ", trainConnection=" + trainConnection +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                '}';
    }
}
