package eu.nighttrains.timetable.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class RailwayStationDto {
    @Min(0)
    private long id;

    @NotBlank
    private String name;

    public RailwayStationDto() {}

    public RailwayStationDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RailwayStationDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
