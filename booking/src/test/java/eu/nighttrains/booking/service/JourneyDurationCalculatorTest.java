package eu.nighttrains.booking.service;

import eu.nighttrains.timetable.dto.RailwayStationConnectionDto;
import eu.nighttrains.timetable.dto.RailwayStationDto;
import eu.nighttrains.timetable.dto.TrainConnectionDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class JourneyDurationCalculatorTest {
	@Test
	public void testCalculateJourneyDurationOneDayTrip() {
		List<RailwayStationConnectionDto> stops = new ArrayList<>();
		stops.add(new RailwayStationConnectionDto(
			new RailwayStationDto(100, "A"),
			new RailwayStationDto(101, "B"),
			new TrainConnectionDto(200, "TrainA", new ArrayList<>()),
			LocalTime.of(19, 0),
			LocalTime.of(23, 0)
		));
		stops.add(new RailwayStationConnectionDto(
				new RailwayStationDto(101, "B"),
				new RailwayStationDto(102, "C"),
				new TrainConnectionDto(200, "TrainA", new ArrayList<>()),
				LocalTime.of(23, 2),
				LocalTime.of(23, 59)
		));
		assertEquals(0, JourneyDurationCalculator.calculateJourneyDurationInDays(stops));
	}


	@Test
	public void testCalculateJourneyDurationTwoDayTrip() {
		List<RailwayStationConnectionDto> stops = new ArrayList<>();
		stops.add(new RailwayStationConnectionDto(
				new RailwayStationDto(100, "A"),
				new RailwayStationDto(101, "B"),
				new TrainConnectionDto(200, "TrainA", new ArrayList<>()),
				LocalTime.of(19, 0),
				LocalTime.of(23, 0)
		));
		stops.add(new RailwayStationConnectionDto(
				new RailwayStationDto(101, "B"),
				new RailwayStationDto(102, "C"),
				new TrainConnectionDto(200, "TrainA", new ArrayList<>()),
				LocalTime.of(23, 2),
				LocalTime.of(0, 1)
		));
		assertEquals(1, JourneyDurationCalculator.calculateJourneyDurationInDays(stops));
	}


	@Test
	public void testCalculateJourneyDurationThreeDayTrip() {
		List<RailwayStationConnectionDto> stops = new ArrayList<>();
		stops.add(new RailwayStationConnectionDto(
				new RailwayStationDto(100, "A"),
				new RailwayStationDto(101, "B"),
				new TrainConnectionDto(200, "TrainA", new ArrayList<>()),
				LocalTime.of(19, 0),
				LocalTime.of(23, 0)
		));
		stops.add(new RailwayStationConnectionDto(
				new RailwayStationDto(101, "B"),
				new RailwayStationDto(102, "C"),
				new TrainConnectionDto(200, "TrainA", new ArrayList<>()),
				LocalTime.of(23, 2),
				LocalTime.of(6, 0)
		));
		stops.add(new RailwayStationConnectionDto(
				new RailwayStationDto(102, "C"),
				new RailwayStationDto(103, "D"),
				new TrainConnectionDto(200, "TrainA", new ArrayList<>()),
				LocalTime.of(6, 2),
				LocalTime.of(0, 0)
		));
		assertEquals(2, JourneyDurationCalculator.calculateJourneyDurationInDays(stops));
	}
}
