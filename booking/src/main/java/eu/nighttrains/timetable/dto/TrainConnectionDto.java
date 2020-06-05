package eu.nighttrains.timetable.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrainConnectionDto {
    @PositiveOrZero
    private long id;

    @NotBlank
    private String code;

    private List<@Valid TrainCarDto> trainCars;

    public TrainConnectionDto() {
        this.trainCars = new ArrayList<>();
    }

    public TrainConnectionDto(long id, String code, List<TrainCarDto> trainCars) {
        this.id = id;
        this.code = code;
        this.trainCars = trainCars;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<TrainCarDto> getTrainCars() {
        return this.trainCars;
    }

    public void setTrainCars(List<TrainCarDto> trainCars) {
        this.trainCars = trainCars;
    }

    @Override
    public String toString() {
        return "TrainConnectionDto{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", trainCars=" + trainCars +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainConnectionDto that = (TrainConnectionDto) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
