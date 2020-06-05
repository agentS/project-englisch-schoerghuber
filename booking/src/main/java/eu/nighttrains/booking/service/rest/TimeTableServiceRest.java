package eu.nighttrains.booking.service.rest;

import eu.nighttrains.booking.service.NoConnectionsAvailableException;
import eu.nighttrains.booking.service.TimeTableService;
import eu.nighttrains.booking.service.TimeTableServiceNotAvailableException;
import eu.nighttrains.timetable.dto.RailwayStationConnectionDto;
import eu.nighttrains.timetable.dto.TrainCarDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeTableServiceRest implements TimeTableService {
	private final TimeTableRestClientFeignWrapper timeTableRestClient;
	private final CircuitBreakerFactory circuitBreakerFactory;

	public TimeTableServiceRest(
			@Autowired TimeTableRestClientFeignWrapper timeTableRestClient,
			@Autowired CircuitBreakerFactory circuitBreakerFactory
	) {
		this.timeTableRestClient = timeTableRestClient;
		this.circuitBreakerFactory = circuitBreakerFactory;
	}

	@Override
	public List<RailwayStationConnectionDto> getRailwayConnections(Long originId, Long destinationId) {
		try {
			return this.circuitBreakerFactory.create("getRailwayConnections").run(
					() -> this.timeTableRestClient.getRailwayConnections(originId, destinationId)
			);
		} catch (NoFallbackAvailableException exception) {
			if (exception.getCause() instanceof NoConnectionsAvailableException) {
				throw ((NoConnectionsAvailableException) exception.getCause());
			}
			throw new TimeTableServiceNotAvailableException(exception);
		}
	}

	@Override
	public List<TrainCarDto> getTrainCarsForConnectionId(Long id) {
		return this.circuitBreakerFactory.create("getTrainCarsForConnectionId").run(
				() -> this.timeTableRestClient.getTrainCarsForConnectionId(id),
				(exception) -> { throw new TimeTableServiceNotAvailableException(exception); }
		);
	}

	@Override
	public List<TrainCarDto> getTrainCarsForConnectionCode(String code) {
		return this.circuitBreakerFactory.create("getTrainCarsForConnectionCode").run(
				() -> this.timeTableRestClient.getTrainCarsForConnectionCode(code),
				(exception) -> { throw new TimeTableServiceNotAvailableException(exception); }
		);
	}
}
