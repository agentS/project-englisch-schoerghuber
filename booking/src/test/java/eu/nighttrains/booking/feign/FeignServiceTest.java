package eu.nighttrains.booking.feign;

import eu.nighttrains.booking.service.NoConnectionsAvailableException;
import eu.nighttrains.booking.service.rest.TimeTableServiceRest;
import eu.nighttrains.timetable.dto.RailwayStationConnectionDto;
import eu.nighttrains.timetable.dto.TrainCarDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FeignServiceTest {
	@Autowired
	private TimeTableServiceRest timeTableServiceRest;

	@Test
	void fetchConnections() {
		final Long originId = 0L;
		final Long destinationId = 14L;

		assertDoesNotThrow(() -> {
			List<RailwayStationConnectionDto> connections = this.timeTableServiceRest.getRailwayConnections(originId, destinationId);
			assertNotNull(connections);
			assertEquals(14, connections.size());
			assertEquals("NJ 466", connections.get(0).getTrainConnection().getCode());
			assertEquals("Wien Hauptbahnhof", connections.get(0).getDepartureStation().getName());
			assertEquals("Zürich", connections.get(13).getArrivalStation().getName());
		});

	}

	@Test
	void fetchUnknownConnection() {
		final Long originId = 340L;
		final Long destinationId = 124L;

		assertThrows(NoConnectionsAvailableException.class, () -> {
			List<RailwayStationConnectionDto> connections = this.timeTableServiceRest.getRailwayConnections(originId, destinationId);
			assertNull(connections);
		});
	}

	@Test
	void fetchTrainCars() {
		final Long connectionId = 1L;
		final String connectionCode = "NJ 467";

		assertDoesNotThrow(() -> {
			List<TrainCarDto> trainCarsFromId = timeTableServiceRest.getTrainCarsForConnectionId(connectionId);
			List<TrainCarDto> trainCarsFromCode = timeTableServiceRest.getTrainCarsForConnectionCode(connectionCode);
			assertNotNull(trainCarsFromId);
			assertNotNull(trainCarsFromCode);
			assertEquals(7, trainCarsFromId.size());
			assertEquals(trainCarsFromId.size(), trainCarsFromCode.size());
		});
	}

}
