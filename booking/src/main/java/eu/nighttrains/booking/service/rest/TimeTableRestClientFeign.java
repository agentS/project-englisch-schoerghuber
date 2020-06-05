package eu.nighttrains.booking.service.rest;

import eu.nighttrains.timetable.dto.RailwayStationConnectionDto;
import eu.nighttrains.timetable.dto.TrainCarDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "timeTableRestClientFeign", url = "${timetable.url}", primary = false)
@Profile("!test")
public interface TimeTableRestClientFeign {
    @RequestMapping(method = RequestMethod.GET, value = "/destinations/from/{originId}/to/{destinationId}")
    List<RailwayStationConnectionDto> getRailwayConnections(@PathVariable("originId") Long originId,
                                                            @PathVariable("destinationId") Long destinationId);

    @RequestMapping(method = RequestMethod.GET, value = "/trainConnection/{id}/cars")
    List<TrainCarDto> getTrainCarsForConnectionId(@PathVariable("id") Long id);

    @RequestMapping(method = RequestMethod.GET, value = "/trainConnection/code/{code}/cars")
    List<TrainCarDto> getTrainCarsForConnectionCode(@PathVariable("code") String code);
}
