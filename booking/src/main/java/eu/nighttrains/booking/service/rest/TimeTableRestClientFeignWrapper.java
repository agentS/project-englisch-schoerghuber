package eu.nighttrains.booking.service.rest;

import eu.nighttrains.booking.service.NoConnectionsAvailableException;
import eu.nighttrains.timetable.dto.RailwayStationConnectionDto;
import eu.nighttrains.timetable.dto.TrainCarDto;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TimeTableRestClientFeignWrapper {

    @Autowired
    private TimeTableRestClientFeign timeTableRestClientFeign;

    public List<RailwayStationConnectionDto> getRailwayConnections(Long originId,
                                                                   Long destinationId) throws NoConnectionsAvailableException {
        try {
            return timeTableRestClientFeign.getRailwayConnections(originId, destinationId);
        } catch (FeignException.NotFound notFoundException) {
            throw new NoConnectionsAvailableException();
        }
    }

    public List<TrainCarDto> getTrainCarsForConnectionId(Long id) {
        return timeTableRestClientFeign.getTrainCarsForConnectionId(id);
    }

    public List<TrainCarDto> getTrainCarsForConnectionCode(String code) {
        return timeTableRestClientFeign.getTrainCarsForConnectionCode(code);
    }
}
