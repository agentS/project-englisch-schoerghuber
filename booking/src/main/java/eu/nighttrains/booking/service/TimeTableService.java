package eu.nighttrains.booking.service;

import eu.nighttrains.timetable.dto.RailwayStationConnectionDto;
import eu.nighttrains.timetable.dto.TrainCarDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface TimeTableService {
	List<RailwayStationConnectionDto> getRailwayConnections(Long originId, Long destinationId);

	List<TrainCarDto> getTrainCarsForConnectionId(Long id);

	List<TrainCarDto> getTrainCarsForConnectionCode(String code);
}
