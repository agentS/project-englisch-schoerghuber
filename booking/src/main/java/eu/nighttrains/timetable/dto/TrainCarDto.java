package eu.nighttrains.timetable.dto;

import eu.nighttrains.timetable.model.TrainCarType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class TrainCarDto {
    @Min(0)
    private long id;

    @Min(0)
    private int number;

    @NotNull
    private TrainCarType type;

    @Min(1)
    private int capacity;

    public TrainCarDto() {}

    public TrainCarDto(long id, int number, TrainCarType type, int capacity) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.capacity = capacity;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public TrainCarType getType() {
        return this.type;
    }

    public void setType(TrainCarType type) {
        this.type = type;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "TrainCarDto{" +
                "id=" + id +
                ", number=" + number +
                ", type=" + type +
                ", capacity=" + capacity +
                '}';
    }
}
